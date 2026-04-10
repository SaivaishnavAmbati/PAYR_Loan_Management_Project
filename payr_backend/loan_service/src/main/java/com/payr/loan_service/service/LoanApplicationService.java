package com.payr.loan_service.service;

import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.dto.LoanApprovalResponseDto;
import com.payr.loan_service.dto.LoanOfficerApplicationResponseDto;
import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LoanApplicationService {
    LoanApplyResponseDto applyLoan(LoanApplyRequestDto request);
    List<LoanApplyResponseDto> getAllAppliedLoans();
    List<LoanApplyResponseDto> getLoansByUserId(Long userId);
    List<LoanOfficerApplicationResponseDto> getApplicationsByStatus(LoanStatus status);
    List<LoanOfficerApplicationResponseDto> getAllApplications();
    LoanApprovalResponseDto approveLoan(Integer loanId);
    LoanApprovalResponseDto rejectLoan(Integer loanId);
}


