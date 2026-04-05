package com.payr.loan_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanApplicationValidationResponse {

    private boolean valid;
    private String message;

    public LoanApplicationValidationResponse() {}

    public LoanApplicationValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

}