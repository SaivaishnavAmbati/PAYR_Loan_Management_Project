package com.payr.loan_service.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LoanApplyResponseDto {
    private Integer loanId;
    private Integer loanTypeId;
    private BigDecimal requestedAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private Integer emiDueDay;
    private LocalDateTime createdAt;
    private List<Long> documentIds; // Return document references

    // Getters and Setters
    public Integer getLoanId() {
        return loanId;
    }
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
    public Integer getLoanTypeId() {
        return loanTypeId;
    }
    public void setLoanTypeId(Integer loanTypeId) { this.loanTypeId = loanTypeId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Integer getEmiDueDay() { return emiDueDay; }
    public void setEmiDueDay(Integer emiDueDay) { this.emiDueDay = emiDueDay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Long> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<Long> documentIds) { this.documentIds = documentIds; }
}
