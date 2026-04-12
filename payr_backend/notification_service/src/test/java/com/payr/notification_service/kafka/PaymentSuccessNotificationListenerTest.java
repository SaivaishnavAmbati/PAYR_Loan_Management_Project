package com.payr.notification_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PaymentSuccessNotificationListenerTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PaymentSuccessNotificationListener listener;

    @Test
    void testHandlePaymentSuccess_ValidEvent() {
        log.info("Testing handlePaymentSuccess: Valid Event scenario");
        String jsonMessage = "{\"loanId\":1,\"userEmail\":\"test@domain.com\",\"amountPaid\":500.00,\"status\":\"SUCCESS\"}";

        listener.handlePaymentSuccess(jsonMessage);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@domain.com", sentMessage.getTo()[0]);
        assertEquals("Payment Successful - PAYR Loan Management", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("500"));
        log.info("Finished testing handlePaymentSuccess: Valid Event scenario");
    }

    @Test
    void testHandlePaymentSuccess_FailedEvent() {
        log.info("Testing handlePaymentSuccess: Failed Event scenario");
        String jsonMessage = "{\"loanId\":1,\"userEmail\":\"test@domain.com\",\"amountPaid\":500.00,\"status\":\"FAILED\"}";

        listener.handlePaymentSuccess(jsonMessage);

        // Mail should not be sent for failed payments
        verifyNoInteractions(mailSender);
        log.info("Finished testing handlePaymentSuccess: Failed Event scenario");
    }
}
