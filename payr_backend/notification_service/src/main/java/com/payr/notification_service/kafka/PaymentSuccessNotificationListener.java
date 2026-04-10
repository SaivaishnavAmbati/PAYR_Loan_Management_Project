package com.payr.notification_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PaymentSuccessNotificationListener {

    @Autowired
    private JavaMailSender mailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-success", groupId = "notification-service-group")
    public void handlePaymentSuccess(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String userEmail = event.get("userEmail").asText();
            String amountPaid = event.get("amountPaid").asText();
            String status = event.get("status").asText();

            if ("SUCCESS".equals(status)) {
                sendEmail(userEmail, amountPaid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String toAddress, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setSubject("Payment Successful - PAYR Loan Management");
        message.setText("Dear Customer,\n\nWe have successfully received your payment of INR " + amount + ".\nThank you for using PAYR!\n\nRegards,\nPAYR Team");
        mailSender.send(message);
        System.out.println("Payment success email sent to " + toAddress);
    }
}
