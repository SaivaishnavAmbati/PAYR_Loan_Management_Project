package com.payr.loan_service.controller;


import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import com.payr.loan_service.service.LoanTypeService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loanTypes")
public class LoanTypeController {

    private final LoanTypeService loanTypeService;

    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }

    @PostMapping("/createLoanType")
    public ResponseEntity<LoanTypeResponseDto> createLoanType(@Valid @RequestBody LoanTypeRequestDto request) {
        return new ResponseEntity<>(loanTypeService.createLoanType(request), HttpStatus.CREATED);
    }

    @GetMapping("/getLoans")
    public ResponseEntity<List<LoanTypeResponseDto>> getActiveLoanTypes() {
        return new ResponseEntity<>(loanTypeService.getAllActiveLoanTypes(), HttpStatus.OK);
    }

    @GetMapping("getLoanById/{id}")
    public ResponseEntity<LoanTypeResponseDto> getLoanTypeById(@PathVariable Integer id)  {
        return new ResponseEntity<>(loanTypeService.getLoanTypeById(id), HttpStatus.OK);
    }
}

