package com.payr.loan_service.service.impl;

import com.payr.loan_service.client.DocumentClient;
import com.payr.loan_service.client.NotificationClient;
import com.payr.loan_service.dto.*;
import com.payr.loan_service.dto.feignDto.NotificationRequestDto;
import com.payr.loan_service.exception.DocumentServiceUnavailable;
import com.payr.loan_service.exception.LoanNotFoundException;
import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.model.LoanType;
import com.payr.loan_service.repository.LoanApplicationRepository;
import com.payr.loan_service.repository.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceImplTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DocumentClient documentClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    private LoanApplyRequestDto requestDto;
    private LoanType loanType;
    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        requestDto = new LoanApplyRequestDto();
        requestDto.setUserId(1L);
        requestDto.setLoanTypeId(1);
        requestDto.setRequestedAmount(new BigDecimal("5000"));
        requestDto.setTenureMonths(12);
        requestDto.setEmail("test@example.com");

        loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinAmount(new BigDecimal("1000"));
        loanType.setMaxAmount(new BigDecimal("10000"));
        loanType.setMinTenureMonths(6);
        loanType.setMaxTenureMonths(24);
        loanType.setInterestRate(10.5);

        loanApplication = new LoanApplication();
        loanApplication.setLoanId(101);
        loanApplication.setUserId(1L);
        loanApplication.setLoanTypes(loanType);
        loanApplication.setStatus(LoanStatus.PENDING);
    }

    @Test
    void applyLoan_Success() {
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);
        when(modelMapper.map(any(), eq(LoanApplyResponseDto.class))).thenReturn(new LoanApplyResponseDto());

        LoanApplyResponseDto result = loanApplicationService.applyLoan(requestDto);

        assertNotNull(result);
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
        verify(notificationClient, times(1)).sendNotification(any(NotificationRequestDto.class));
    }

    @Test
    void applyLoan_WithFiles_Success() {
        MultipartFile file = mock(MultipartFile.class);
        requestDto.setFiles(List.of(file));
        DocumentResponseDTO docResponse = new DocumentResponseDTO();
        docResponse.setId(99L);

        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));
        when(documentClient.upload(eq(1L), any(MultipartFile.class))).thenReturn(docResponse);
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);
        when(modelMapper.map(any(), eq(LoanApplyResponseDto.class))).thenReturn(new LoanApplyResponseDto());

        loanApplicationService.applyLoan(requestDto);

        verify(documentClient, times(1)).upload(anyLong(), any());
    }

    @Test
    void applyLoan_InvalidUserId_ThrowsException() {
        requestDto.setUserId(null);
        assertThrows(ResponseStatusException.class, () -> loanApplicationService.applyLoan(requestDto));
    }

    @Test
    void applyLoan_LoanTypeNotFound_ThrowsException() {
        when(loanTypeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> loanApplicationService.applyLoan(requestDto));
    }

    @Test
    void applyLoan_AmountOutOfRange_ThrowsException() {
        requestDto.setRequestedAmount(new BigDecimal("20000"));
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));
        assertThrows(ResponseStatusException.class, () -> loanApplicationService.applyLoan(requestDto));
    }

    @Test
    void documentFallback_ThrowsException() {
        assertThrows(DocumentServiceUnavailable.class, () -> 
            loanApplicationService.documentFallback(requestDto, new RuntimeException("Service Down"))
        );
    }

    @Test
    void approveLoan_Success() {
        when(loanApplicationRepository.findById(101)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any())).thenReturn(loanApplication);

        LoanApprovalResponseDto result = loanApplicationService.approveLoan(101);

        assertEquals(LoanStatus.APPROVED, result.getStatus());
        verify(notificationClient).sendNotification(any());
    }

    @Test
    void approveLoan_LoanNotFound_ThrowsException() {
        when(loanApplicationRepository.findById(101)).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanApplicationService.approveLoan(101));
    }

    @Test
    void rejectLoan_Success() {
        when(loanApplicationRepository.findById(101)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any())).thenReturn(loanApplication);

        LoanApprovalResponseDto result = loanApplicationService.rejectLoan(101);

        assertEquals(LoanStatus.REJECTED, result.getStatus());
        verify(notificationClient).sendNotification(any());
    }

    @Test
    void getLoansByUserId_Success() {
        when(loanApplicationRepository.findByUserId(1L)).thenReturn(List.of(loanApplication));
        when(modelMapper.map(any(), eq(LoanApplyResponseDto.class))).thenReturn(new LoanApplyResponseDto());

        List<LoanApplyResponseDto> result = loanApplicationService.getLoansByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAllAppliedLoans_EmptyList_ThrowsException() {
        when(loanApplicationRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResponseStatusException.class, () -> loanApplicationService.getAllAppliedLoans());
    }
}
