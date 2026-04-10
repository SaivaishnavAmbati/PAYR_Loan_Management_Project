package com.payr.payment_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private Integer loanId;
    private String userEmail;
    private BigDecimal amount;
}
