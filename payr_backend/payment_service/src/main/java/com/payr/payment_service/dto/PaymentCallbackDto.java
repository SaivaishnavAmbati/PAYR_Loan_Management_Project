package com.payr.payment_service.dto;

import lombok.Data;

@Data
public class PaymentCallbackDto {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
