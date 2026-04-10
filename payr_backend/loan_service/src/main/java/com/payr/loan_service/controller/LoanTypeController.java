package com.payr.loan_service.controller;


import com.payr.loan_service.config.SecurityUtils;
import com.payr.loan_service.dto.LoanApplicationValidationResponse;
import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import com.payr.loan_service.service.LoanTypeService;
import com.payr.loan_service.service.impl.LoanApplicationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans/loanTypes")
public class LoanTypeController {

    private final LoanTypeService loanTypeService;

    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }

    @PostMapping("/admin/createLoanType")
    public ResponseEntity<LoanTypeResponseDto> createLoanType(@Valid @RequestBody LoanTypeRequestDto request) {
        return new ResponseEntity<>(loanTypeService.createLoanType(request), HttpStatus.CREATED);
    }

    @GetMapping("/getLoans")
    public ResponseEntity<List<LoanTypeResponseDto>> getActiveLoanTypes() {
        return new ResponseEntity<>(loanTypeService.getAllActiveLoanTypes(), HttpStatus.OK);
    }

    @GetMapping("/getLoanById/{id}")
    public ResponseEntity<LoanTypeResponseDto> getLoanTypeById(@PathVariable Integer id)  {
        return new ResponseEntity<>(loanTypeService.getLoanTypeById(id), HttpStatus.OK);
    }

    // Validate checkout before placing order
    @PostMapping("/loan/admin/validate")
    public ResponseEntity<LoanApplicationValidationResponse> validateCheckout() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(LoanApplicationServiceImpl.validateCheckout(userId));
    }
}

