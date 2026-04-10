package com.payr.loan_service.controller;

import com.payr.loan_service.config.SecurityUtils;
import com.payr.loan_service.dto.LoanApplicationValidationResponse;
import com.payr.loan_service.dto.LoanOfficerApplicationResponseDto;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.service.LoanApplicationService;
import com.payr.loan_service.service.impl.LoanApplicationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans/loanOfficer")
public class LoanOfficerController {

    private final LoanApplicationService loanOfficerService;

    public LoanOfficerController(LoanApplicationService loanOfficerService) {
        this.loanOfficerService = loanOfficerService;
    }

    @GetMapping("/applications")
    public ResponseEntity<List<LoanOfficerApplicationResponseDto>> getApplicationsByStatus( @RequestParam LoanStatus status) {

        return ResponseEntity.ok(
                loanOfficerService.getApplicationsByStatus(status));
    }

    @GetMapping("/applications/all")
    public ResponseEntity<List<LoanOfficerApplicationResponseDto>> getAllApplications() {

        return ResponseEntity.ok(
                loanOfficerService.getAllApplications());
    }

    // Validate checkout before placing order
    @PostMapping("/officercheck/validate")
    public ResponseEntity<LoanApplicationValidationResponse> validateCheckout() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(LoanApplicationServiceImpl.validateCheckout(userId));
    }

}
