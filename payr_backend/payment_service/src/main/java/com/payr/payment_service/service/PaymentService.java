package com.payr.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.payment_service.dto.PaymentCallbackDto;
import com.payr.payment_service.dto.PaymentEvent;
import com.payr.payment_service.dto.PaymentRequestDto;
import com.payr.payment_service.model.PaymentStatus;
import com.payr.payment_service.model.PaymentTransaction;
import com.payr.payment_service.repository.PaymentTransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    // ✅ Use application.properties instead (recommended)
    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @CircuitBreaker(name = "razorpay", fallbackMethod = "fallbackCreateOrder")
    public Map<String, Object> createOrder(PaymentRequestDto requestDto) throws RazorpayException {

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();

        BigDecimal amountInPaise = requestDto.getAmount().multiply(new BigDecimal("100"));

        orderRequest.put("amount", amountInPaise.intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + UUID.randomUUID().toString().substring(0, 8));

        Order order = razorpay.orders.create(orderRequest);
        String razorpayOrderId = order.get("id");

        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .loanId(requestDto.getLoanId())
                .userEmail(requestDto.getUserEmail())
                .amountPaid(requestDto.getAmount())
                .status(PaymentStatus.PENDING)
                .razorpayOrderId(razorpayOrderId)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentTransactionRepository.save(transaction);

        // ✅ Return both orderId + amount
        return Map.of(
                "orderId", razorpayOrderId,
                "amount", amountInPaise.intValue()
        );
    }

    public Map<String, Object> fallbackCreateOrder(PaymentRequestDto requestDto, Throwable t) {
        throw new RuntimeException("Payment gateway (Razorpay) is unavailable: " + t.getMessage());
    }

    public boolean verifyCallback(PaymentCallbackDto callbackDto) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", callbackDto.getRazorpayOrderId());
            options.put("razorpay_payment_id", callbackDto.getRazorpayPaymentId());
            options.put("razorpay_signature", callbackDto.getRazorpaySignature());

            boolean status = Utils.verifyPaymentSignature(options, keySecret);

            PaymentTransaction transaction = paymentTransactionRepository
                    .findByRazorpayOrderId(callbackDto.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            if (status) {
                transaction.setStatus(PaymentStatus.SUCCESS);
                transaction.setRazorpayPaymentId(callbackDto.getRazorpayPaymentId());
                paymentTransactionRepository.save(transaction);

                // ✅ Kafka event
                PaymentEvent event = PaymentEvent.builder()
                        .loanId(transaction.getLoanId())
                        .userEmail(transaction.getUserEmail())
                        .amountPaid(transaction.getAmountPaid())
                        .status("SUCCESS")
                        .build();

                String jsonEvent = objectMapper.writeValueAsString(event);
                kafkaTemplate.send("payment-success", jsonEvent);

                System.out.println("✅ Kafka event sent: " + jsonEvent);

                return true;
            } else {
                transaction.setStatus(PaymentStatus.FAILED);
                paymentTransactionRepository.save(transaction);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}