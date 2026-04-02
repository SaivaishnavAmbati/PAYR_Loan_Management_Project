package com.payr.loan_service.dto;


import com.payr.loan_service.model.LoanStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public class LoanApprovalResponseDto {

    private Integer loanId;
    private LoanStatus status;
    private String message;

    public LoanApprovalResponseDto(Integer loanId, LoanStatus status, String message) {
        this.loanId = loanId;
        this.status = status;
        this.message = message;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


