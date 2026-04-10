package com.payr.loan_service.controller;

import com.payr.loan_service.config.SecurityUtils;
import com.payr.loan_service.dto.LoanApplicationValidationResponse;
import com.payr.loan_service.dto.LoanApprovalResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import com.payr.loan_service.service.impl.LoanApplicationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans/loanOfficer")
public class LoanApprovalController {

    private final LoanApplicationService approvalService;

    public LoanApprovalController(LoanApplicationService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping("/approve/{loanId}")
    public ResponseEntity<LoanApprovalResponseDto> approveLoan(
            @PathVariable Integer loanId) {

        return ResponseEntity.ok(approvalService.approveLoan(loanId));
    }

    @PostMapping("/reject/{loanId}")
    public ResponseEntity<LoanApprovalResponseDto> rejectLoan(
            @PathVariable Integer loanId) {

        return ResponseEntity.ok(approvalService.rejectLoan(loanId));
    }
    // Validate checkout before placing order
    @PostMapping("/permissions/validate")
    public ResponseEntity<LoanApplicationValidationResponse> validateCheckout() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(LoanApplicationServiceImpl.validateCheckout(userId));
    }
}

