package com.payr.loan_service.controller;

import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loanApplication")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<LoanApplyResponseDto> applyLoan(@RequestBody LoanApplyRequestDto request) {
        LoanApplyResponseDto response = loanApplicationService.applyLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<LoanApplyResponseDto>> getUserLoans(@PathVariable Integer userId) {
//        List<LoanApplyResponseDto> loans = loanApplicationService.getAppliedLoansByUserId(userId);
//        return new ResponseEntity<>(loans, HttpStatus.OK);
//    }

    @GetMapping("/all")
    public ResponseEntity<List<LoanApplyResponseDto>> getAllLoans() {
        List<LoanApplyResponseDto> loans = loanApplicationService.getAllAppliedLoans();
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }
}

