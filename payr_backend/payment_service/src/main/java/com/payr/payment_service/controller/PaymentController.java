package com.payr.payment_service.controller;


import com.payr.payment_service.dto.PaymentCallbackDto;
import com.payr.payment_service.dto.PaymentRequestDto;
import com.payr.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/payment")   // ✅ match your HTML URL
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequestDto requestDto) {
        try {
            Map<String, Object> response = paymentService.createOrder(requestDto);
            return ResponseEntity.ok(response); // ✅ directly return map
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")   // ✅ match your HTML verify call
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentCallbackDto callbackDto) {

        boolean isValid = paymentService.verifyCallback(callbackDto);

        if (isValid) {
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "FAILED"));
        }
    }
}