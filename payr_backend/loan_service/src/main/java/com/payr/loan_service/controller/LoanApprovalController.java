package com.payr.loan_service.controller;

import com.payr.loan_service.dto.LoanApprovalResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loanOfficer")
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
}

