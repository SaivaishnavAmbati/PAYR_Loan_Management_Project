package com.payr.loan_service.service;



import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;

import java.util.List;

public interface LoanApplicationService {
    LoanApplyResponseDto applyLoan(LoanApplyRequestDto request);


//    List<LoanApplyResponseDto> getAppliedLoansByUserId(Integer userId);

    List<LoanApplyResponseDto> getAllAppliedLoans(); // new method
}

