package com.payr.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payr.payment_service.dto.PaymentCallbackDto;
import com.payr.payment_service.dto.PaymentRequestDto;
import com.payr.payment_service.model.PaymentStatus;
import com.payr.payment_service.model.PaymentTransaction;
import com.payr.payment_service.repository.PaymentTransactionRepository;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "keyId", "test_key_id");
        ReflectionTestUtils.setField(paymentService, "keySecret", "test_secret");
    }

    @Test
    void testFallbackCreateOrder() {
        log.info("Testing testFallbackCreateOrder: Gateway Unavailable scenario");
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setAmount(new BigDecimal("5000"));
        requestDto.setLoanId(1);
        requestDto.setUserEmail("test@test.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.fallbackCreateOrder(requestDto, new Exception("Network Error"));
        });
        
        assertTrue(exception.getMessage().contains("Payment gateway (Razorpay) is unavailable"));
        log.info("Finished testing testFallbackCreateOrder: Gateway Unavailable scenario");
    }

    @Test
    void testVerifyCallback_TransactionNotFound() throws RazorpayException {
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();
        callbackDto.setRazorpayOrderId("order_abc");
        callbackDto.setRazorpayPaymentId("pay_abc");
        callbackDto.setRazorpaySignature("signature123");

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), eq("test_secret")))
                    .thenReturn(true);

            when(paymentTransactionRepository.findByRazorpayOrderId("order_abc"))
                    .thenReturn(Optional.empty());

            boolean result = paymentService.verifyCallback(callbackDto);
            
            assertFalse(result); // Returns false because Exception is caught in verifyCallback
        }
    }

    @Test
    void testVerifyCallback_ValidSignature() throws JsonProcessingException {
        log.info("Testing testVerifyCallback: Valid Signature scenario");
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();
        callbackDto.setRazorpayOrderId("order_abc");
        callbackDto.setRazorpayPaymentId("pay_abc");
        callbackDto.setRazorpaySignature("signature123");

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setLoanId(10);
        transaction.setUserEmail("test@test.com");
        transaction.setAmountPaid(new BigDecimal("500"));
        transaction.setStatus(PaymentStatus.PENDING);

        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.verifyPaymentSignature(any(JSONObject.class), eq("test_secret")))
                    .thenReturn(true);

            when(paymentTransactionRepository.findByRazorpayOrderId("order_abc"))
                    .thenReturn(Optional.of(transaction));

            boolean result = paymentService.verifyCallback(callbackDto);

            assertTrue(result);
            assertEquals(PaymentStatus.SUCCESS, transaction.getStatus());
            assertEquals("pay_abc", transaction.getRazorpayPaymentId());
            
            verify(paymentTransactionRepository, times(1)).save(transaction);
            verify(kafkaTemplate, times(1)).send(eq("payment-success"), any(String.class));
            log.info("Finished testing testVerifyCallback: Valid Signature scenario");
        }
    }
}
