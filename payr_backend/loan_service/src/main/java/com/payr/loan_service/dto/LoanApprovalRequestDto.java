package com.payr.loan_service.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class LoanApprovalRequestDto {
    private String remarks;

    public LoanApprovalRequestDto(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
