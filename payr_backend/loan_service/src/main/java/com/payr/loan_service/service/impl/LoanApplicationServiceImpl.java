package com.payr.loan_service.service.impl;

import com.payr.loan_service.client.DocumentClient;
import com.payr.loan_service.dto.*;
import com.payr.loan_service.dto.feignDto.NotificationRequestDto;
import com.payr.loan_service.exception.LoanNotFoundException;
import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.model.LoanType;
import com.payr.loan_service.repository.LoanApplicationRepository;
import com.payr.loan_service.repository.LoanTypeRepository;
import com.payr.loan_service.service.LoanApplicationService;
//import com.payr.loan_service.service.feign.NotificationClient;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ModelMapper modelMapper;
    private final DocumentClient documentClient;
//    private final NotificationClient notificationClient;

    public LoanApplicationServiceImpl(LoanApplicationRepository loanApplicationRepository, LoanTypeRepository loanTypeRepository, ModelMapper modelMapper, DocumentClient documentClient) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.modelMapper = modelMapper;
        this.documentClient = documentClient;
//        this.notificationClient = notificationClient;
    }

    @Override
    public LoanApplyResponseDto applyLoan(LoanApplyRequestDto request) {
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan Type not found"));

        if (request.getRequestedAmount() == null ||
                request.getRequestedAmount().compareTo(loanType.getMinAmount()) < 0 ||
                request.getRequestedAmount().compareTo(loanType.getMaxAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Requested amount must be between " + loanType.getMinAmount() + " and " + loanType.getMaxAmount());
        }

        if (request.getTenureMonths() == null ||
                request.getTenureMonths() < loanType.getMinTenureMonths() ||
                request.getTenureMonths() > loanType.getMaxTenureMonths()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tenure must be between " + loanType.getMinTenureMonths() + " and " + loanType.getMaxTenureMonths() + " months"
            );
        }

        List<Long> documentIds = new ArrayList<>();
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                DocumentResponseDTO docResponse = documentClient.upload(request.getUserId(), file);
                documentIds.add(docResponse.getId());
            }
        }

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypes(loanType);
        loanApplication.setUserId(request.getUserId());
        loanApplication.setUserEmail(request.getEmail());
        loanApplication.setRequestedAmount(request.getRequestedAmount());
        loanApplication.setStatus(LoanStatus.PENDING);
        loanApplication.setTenureMonths(request.getTenureMonths());
        loanApplication.setEmiDueDay(request.getEmiDueDay() != null ? request.getEmiDueDay() : 1);
        loanApplication.setInterestRate(loanType.getInterestRate());
        loanApplication.setCreatedAt(LocalDateTime.now());
        loanApplication.setDocumentIds(documentIds);

        LoanApplication saved = loanApplicationRepository.save(loanApplication);

        LoanApplyResponseDto response = modelMapper.map(saved, LoanApplyResponseDto.class);
        response.setLoanTypeId(saved.getLoanTypes().getId());
        response.setDocumentIds(saved.getDocumentIds());
        response.setMinTenureMonths(saved.getLoanTypes().getMinTenureMonths());
        response.setMaxTenureMonths(saved.getLoanTypes().getMaxTenureMonths());

//        // Build notification request manually
//        NotificationRequestDto notification = new NotificationRequestDto();
//        notification.setUserId(request.getUserId());
//        notification.setEmail(request.getEmail());
//        notification.setSubject("Loan Application Submitted");
//        notification.setMessage(
//                "Dear Customer,\n\nYour loan application has been submitted successfully."
//        );
//        notification.setNotificationType("LOAN_APPLIED");
//
//        try {
//            notificationClient.sendNotification(notification);
//        } catch (Exception e) {
//            System.out.println("Notification failed: " + e.getMessage());
//        }
        return response;
    }

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


    @Override
    public List<LoanApplyResponseDto> getLoansByUserId(Long userId) {

        List<LoanApplication> loans = loanApplicationRepository.findByUserId(userId);

        return loans.stream().map(loan -> {

            LoanApplyResponseDto response =
                    modelMapper.map(loan, LoanApplyResponseDto.class);

            response.setLoanTypeId(loan.getLoanTypes().getId());
            response.setDocumentIds(loan.getDocumentIds());
            response.setMinTenureMonths(loan.getLoanTypes().getMinTenureMonths());
            response.setMaxTenureMonths(loan.getLoanTypes().getMaxTenureMonths());

            return response;

        }).toList();
    }


    @Override
    public List<LoanOfficerApplicationResponseDto>getApplicationsByStatus(LoanStatus status) {

        List<LoanApplication> applications =
                loanApplicationRepository.findByStatus(status);

        return applications.stream()
                .map(this::mapToDto)
                .toList();
    }

    private LoanOfficerApplicationResponseDto mapToDto(LoanApplication loan) {

        LoanOfficerApplicationResponseDto dto =
                new LoanOfficerApplicationResponseDto();

        dto.setLoanId(loan.getLoanId());
        dto.setUserId(loan.getUserId());
        dto.setRequestedAmount(loan.getRequestedAmount());
        dto.setTenureMonths(loan.getTenureMonths());
        dto.setStatus(loan.getStatus());
        dto.setCreatedAt(loan.getCreatedAt());

        return dto;
    }

    @Override
    public List<LoanOfficerApplicationResponseDto> getAllApplications() {

        List<LoanApplication> applications =
                loanApplicationRepository.findAll();

        return applications.stream()
                .map(this::mapToDto)
                .toList();
    }

    public LoanApprovalResponseDto approveLoan(Integer loanId) {

            LoanApplication loan = loanApplicationRepository.findById(loanId)
                    .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

            if (loan.getStatus() != LoanStatus.PENDING) {
                throw new IllegalStateException("Loan already processed");
            }

            loan.setStatus(LoanStatus.APPROVED);
            loanApplicationRepository.save(loan);

//        NotificationRequestDto notification = new NotificationRequestDto();
//        notification.setUserId(loan.getUserId());
//        notification.setEmail(loan.getUserEmail());
//        notification.setSubject("Loan Approved");
//        notification.setMessage(
//                "Congratulations! Your loan has been approved."
//        );
//        notification.setNotificationType("LOAN_APPROVED");
//
//        try {
//            notificationClient.sendNotification(notification);
//        } catch (Exception e) {
//            System.out.println("Notification failed: " + e.getMessage());
//        }

        return new LoanApprovalResponseDto(
                loan.getLoanId(),
                LoanStatus.APPROVED,
                "Loan approved successfully"
        );

    }

    public LoanApprovalResponseDto rejectLoan(Integer loanId) {

            LoanApplication loan = loanApplicationRepository.findById(loanId)
                    .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

            if (loan.getStatus() != LoanStatus.PENDING) {
                throw new IllegalStateException("Loan already processed");
            }

            loan.setStatus(LoanStatus.REJECTED);
            loanApplicationRepository.save(loan);
//        NotificationRequestDto notification = new NotificationRequestDto();
//        notification.setUserId(loan.getUserId());
//        notification.setEmail(loan.getUserEmail());
//        notification.setSubject("Loan Rejected");
//        notification.setMessage("We regret to inform you that your loan has been rejected.");
//        notification.setNotificationType("LOAN_REJECTED");
//
//        try {
//            notificationClient.sendNotification(notification);
//        } catch (Exception e) {
//            System.out.println("Notification failed: " + e.getMessage());
//        }

        return new LoanApprovalResponseDto(
                loan.getLoanId(),
                LoanStatus.REJECTED,
                "Loan rejected successfully"
        );
    }
    }

