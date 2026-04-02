package com.payr.loan_service.controller;

import com.payr.loan_service.dto.LoanOfficerApplicationResponseDto;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loanOfficer/applications")
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

}
