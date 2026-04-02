package com.payr.loan_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer loanId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "loan_type_id", nullable = false)
    private LoanType loanTypes;


//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;  // ✅ add this

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private BigDecimal requestedAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private Integer emiDueDay;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @ElementCollection
    @CollectionTable(name = "loan_application_documents", joinColumns = @JoinColumn(name = "loan_id"))
    @Column(name = "document_id")
    private List<Long> documentIds = new ArrayList<>();

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLoanId() { return loanId; }
    public void setLoanId(Integer loanId) { this.loanId = loanId; }
    public LoanType getLoanTypes() { return loanTypes; }
    public void setLoanTypes(LoanType loanTypes) { this.loanTypes = loanTypes; }
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

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

