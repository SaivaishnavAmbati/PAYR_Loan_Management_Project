package com.payr.loan_service.service.impl;

import com.payr.loan_service.service.LoanApplicationService;


import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanType;
import com.payr.loan_service.repository.LoanApplicationRepository;
import com.payr.loan_service.repository.LoanTypeRepository;
import com.payr.loan_service.service.LoanApplicationService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
    public class LoanApplicationServiceImpl implements LoanApplicationService {

        private final LoanApplicationRepository loanApplicationRepository;
        private final LoanTypeRepository loanTypeRepository;
        private final ModelMapper modelMapper;

        public LoanApplicationServiceImpl(
                LoanApplicationRepository loanApplicationRepository,
                LoanTypeRepository loanTypeRepository,
                ModelMapper modelMapper) {
            this.loanApplicationRepository = loanApplicationRepository;
            this.loanTypeRepository = loanTypeRepository;
            this.modelMapper = modelMapper;
        }

        @Override
        public LoanApplyResponseDto applyLoan(LoanApplyRequestDto request) {

            // Validate loan type
            if (request.getLoanTypeId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan Type ID is required");
            }

            LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan Type not found"));

            // Validate requested amount
            if (request.getRequestedAmount() == null ||
                    request.getRequestedAmount().compareTo(loanType.getMinAmount()) < 0 ||
                    request.getRequestedAmount().compareTo(loanType.getMaxAmount()) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Requested amount must be between " + loanType.getMinAmount() + " and " + loanType.getMaxAmount());
            }

            // Validate tenure
            if (request.getTenureMonths() == null ||
                    request.getTenureMonths() < loanType.getTenureMonths()-100 ||
                    request.getTenureMonths() > loanType.getTenureMonths()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tenure must be between " + loanType.getTenureMonths() + " and " + loanType.getTenureMonths() + " months");
            }

            // Map DTO to entity
            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setLoanTypes(loanType);
            loanApplication.setRequestedAmount(request.getRequestedAmount());
            loanApplication.setTenureMonths(request.getTenureMonths());
            loanApplication.setEmiDueDay(request.getEmiDueDay() != null ? request.getEmiDueDay() : 1);
            loanApplication.setInterestRate(loanType.getInterestRate());
            loanApplication.setCreatedAt(LocalDateTime.now());

            // Map document IDs
            if (request.getDocumentIds() != null) {
                loanApplication.setDocumentIds(request.getDocumentIds());
            }

            LoanApplication saved = loanApplicationRepository.save(loanApplication);

            LoanApplyResponseDto response = modelMapper.map(saved, LoanApplyResponseDto.class);
            response.setLoanTypeId(saved.getLoanTypes().getId());
            response.setDocumentIds(saved.getDocumentIds());

            return response;
        }

//        @Override
//        public List<LoanApplyResponseDto> getAppliedLoansByUserId(Integer userId) {
//            if (userId == null) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
//            }
//
//            // Fetch all loans for the user
//            List<LoanApplication> loans = loanApplicationRepository.findByUserId(userId);
//            if (loans.isEmpty()) {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No loans found for user ID: " + userId);
//            }
//
//            // Map to DTOs
//            List<LoanApplyResponseDto> responseList = new ArrayList<>();
//            for (LoanApplication loan : loans) {
//                LoanApplyResponseDto dto = modelMapper.map(loan, LoanApplyResponseDto.class);
//                dto.setLoanTypeId(loan.getLoanTypes().getId());
//                dto.setDocumentIds(loan.getDocumentIds());
//                responseList.add(dto);
//            }
//            return responseList;
//        }

        @Override
        public List<LoanApplyResponseDto> getAllAppliedLoans() {
        List<LoanApplication> loans = loanApplicationRepository.findAll();

        if (loans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No loans found");
        }

        List<LoanApplyResponseDto> responseList = new ArrayList<>();
        for (LoanApplication loan : loans) {
            LoanApplyResponseDto dto = modelMapper.map(loan, LoanApplyResponseDto.class);
            dto.setLoanTypeId(loan.getLoanTypes().getId());
            dto.setDocumentIds(loan.getDocumentIds());
            responseList.add(dto);
        }
        return responseList;
    }


}