package com.payr.loan_service.controller;

import com.payr.loan_service.config.SecurityUtils;
import com.payr.loan_service.dto.LoanApplicationValidationResponse;
import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import com.payr.loan_service.service.impl.LoanApplicationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans/loanApplication")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @Operation(summary = "Apply for loan with document upload")
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoanApplyResponseDto> applyLoan(
            @RequestParam("loanTypeId") Integer loanTypeId,
            @RequestParam("requestedAmount") BigDecimal requestedAmount,
            @RequestParam("tenureMonths") Integer tenureMonths,
            @RequestParam(value = "emiDueDay", required = false) Integer emiDueDay,
            @RequestParam("email") String email,

            @Parameter(
                    description = "Upload documents",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {

        LoanApplyRequestDto request = new LoanApplyRequestDto();
        request.setUserId(SecurityUtils.getCurrentUserId());
        request.setLoanTypeId(loanTypeId);
        request.setRequestedAmount(requestedAmount);
        request.setTenureMonths(tenureMonths);
        request.setEmiDueDay(emiDueDay);
        request.setEmail(email);
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

    // Validate checkout before placing order
    @PostMapping("/loan/apply/validate")
    public ResponseEntity<LoanApplicationValidationResponse> validateCheckout() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(LoanApplicationServiceImpl.validateCheckout(userId));
    }

}
