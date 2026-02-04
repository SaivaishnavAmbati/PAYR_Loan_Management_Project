package com.payr.loan_service.dto;



import java.math.BigDecimal;
import java.util.List;

public class LoanApplyRequestDto {
    private Integer loanTypeId;
    private BigDecimal requestedAmount;
    private Integer tenureMonths;
    private Integer emiDueDay; // optional
    private List<Long> documentIds; // document IDs from DocumentService

    // Getters and Setters
    public Integer getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(Integer loanTypeId) { this.loanTypeId = loanTypeId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Integer getEmiDueDay() { return emiDueDay; }
    public void setEmiDueDay(Integer emiDueDay) { this.emiDueDay = emiDueDay; }
    public List<Long> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<Long> documentIds) { this.documentIds = documentIds; }
}

