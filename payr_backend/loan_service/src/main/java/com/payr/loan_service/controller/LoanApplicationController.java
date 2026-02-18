package com.payr.loan_service.controller;

import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loanApplication")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping(value = "/apply", consumes = {"multipart/form-data"})
    public ResponseEntity<LoanApplyResponseDto> applyLoan(
            @RequestParam Long userId,
            @RequestParam Integer loanTypeId,
            @RequestParam BigDecimal requestedAmount,
            @RequestParam Integer tenureMonths,
            @RequestParam(required = false) Integer emiDueDay,
            @RequestPart(required = false) List<MultipartFile> files) {

        LoanApplyRequestDto request = new LoanApplyRequestDto();
        request.setUserId(userId);
        request.setLoanTypeId(loanTypeId);
        request.setRequestedAmount(requestedAmount);
        request.setTenureMonths(tenureMonths);
        request.setEmiDueDay(emiDueDay);
        request.setFiles(files);

        LoanApplyResponseDto response = loanApplicationService.applyLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/all")
    public ResponseEntity<List<LoanApplyResponseDto>> getAllLoans() {
        List<LoanApplyResponseDto> loans = loanApplicationService.getAllAppliedLoans();
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanApplyResponseDto>> getLoansByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanApplicationService.getLoansByUserId(userId)
        );
    }



}
