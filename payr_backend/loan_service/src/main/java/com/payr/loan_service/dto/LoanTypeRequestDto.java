package com.payr.loan_service.dto;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanTypeRequestDto {

    @NotBlank(message = "Loan name is required")
    private String name;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.1", message = "Interest rate must be greater than 0")
    private Double interestRate;

    @NotNull(message = "Tenure months is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureMonths;

    @NotNull(message = "Min amount is required")
    private BigDecimal minAmount;

    @NotNull(message = "Max amount is required")
    private BigDecimal maxAmount;

    private Boolean active = true;

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}

