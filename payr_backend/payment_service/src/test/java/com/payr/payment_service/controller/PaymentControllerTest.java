package com.payr.payment_service.controller;

import com.payr.payment_service.dto.PaymentCallbackDto;
import com.payr.payment_service.dto.PaymentRequestDto;
import com.payr.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void testCreateOrder_Success() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setAmount(new BigDecimal("1000"));

        when(paymentService.createOrder(requestDto))
                .thenReturn(Map.of(
                        "orderId", "order_123",
                        "amount", 100000
                ));

        ResponseEntity<?> response = paymentController.createOrder(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("order_123", body.get("orderId"));
        assertEquals(100000, body.get("amount"));
    }

    @Test
    void testVerifyPayment_Success() {
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();

        when(paymentService.verifyCallback(callbackDto)).thenReturn(true);

        // ✅ FIXED METHOD NAME
        ResponseEntity<?> response = paymentController.verifyPayment(callbackDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void testVerifyPayment_Failure() {
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();

        when(paymentService.verifyCallback(callbackDto)).thenReturn(false);

        // ✅ FIXED METHOD NAME
        ResponseEntity<?> response = paymentController.verifyPayment(callbackDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("FAILED", ((Map<?, ?>) response.getBody()).get("status"));
    }
}