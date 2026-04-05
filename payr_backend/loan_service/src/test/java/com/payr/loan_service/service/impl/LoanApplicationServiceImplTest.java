package com.payr.loan_service.service.impl;

import com.payr.loan_service.client.DocumentClient;
import com.payr.loan_service.client.NotificationClient;
import com.payr.loan_service.dto.LoanApplicationValidationResponse;
import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.dto.LoanOfficerApplicationResponseDto;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private LoanApplyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new LoanApplyRequestDto();
        requestDto.setUserId(1L);
        requestDto.setLoanTypeId(1);
        requestDto.setRequestedAmount(BigDecimal.valueOf(50000));
        requestDto.setTenureMonths(24);
        requestDto.setEmail("test@test.com");

        loanType = new LoanType();
        loanType.setId(1);
        loanType.setMinAmount(BigDecimal.valueOf(10000));
        loanType.setMaxAmount(BigDecimal.valueOf(100000));
        loanType.setMinTenureMonths(12);
        loanType.setMaxTenureMonths(60);
        loanType.setInterestRate(10.0);

        loanApplication = new LoanApplication();
        loanApplication.setLoanId(1);
        loanApplication.setUserId(1L);
        loanApplication.setLoanTypes(loanType);
        loanApplication.setStatus(LoanStatus.PENDING);

        responseDto = new LoanApplyResponseDto();
        responseDto.setLoanId(1);
    }

    @Test
    void applyLoan_Success() {
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);
        when(modelMapper.map(any(LoanApplication.class), eq(LoanApplyResponseDto.class))).thenReturn(responseDto);

        LoanApplyResponseDto result = loanApplicationService.applyLoan(requestDto);

        assertNotNull(result);
        assertEquals(1, result.getLoanId());
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void applyLoan_InvalidAmount_ShouldThrowException() {
        requestDto.setRequestedAmount(BigDecimal.valueOf(5000)); // less than min amount
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));

        assertThrows(ResponseStatusException.class, () -> {
            loanApplicationService.applyLoan(requestDto);
        });
    }

    @Test
    void applyLoan_InvalidTenure_ShouldThrowException() {
        requestDto.setTenureMonths(6); // less than min tenure
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));

        assertThrows(ResponseStatusException.class, () -> {
            loanApplicationService.applyLoan(requestDto);
        });
    }

    @Test
    void getAllAppliedLoans_Success() {
        when(loanApplicationRepository.findAll()).thenReturn(Collections.singletonList(loanApplication));
        when(modelMapper.map(any(LoanApplication.class), eq(LoanApplyResponseDto.class))).thenReturn(responseDto);

        List<LoanApplyResponseDto> result = loanApplicationService.getAllAppliedLoans();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllAppliedLoans_Empty_ShouldThrowException() {
        when(loanApplicationRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> {
            loanApplicationService.getAllAppliedLoans();
        });
    }

    @Test
    void getLoansByUserId_Success() {
        when(loanApplicationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(loanApplication));
        when(modelMapper.map(any(LoanApplication.class), eq(LoanApplyResponseDto.class))).thenReturn(responseDto);

        List<LoanApplyResponseDto> result = loanApplicationService.getLoansByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getApplicationsByStatus_Success() {
        when(loanApplicationRepository.findByStatus(LoanStatus.PENDING)).thenReturn(Collections.singletonList(loanApplication));

        List<LoanOfficerApplicationResponseDto> result = loanApplicationService.getApplicationsByStatus(LoanStatus.PENDING);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void validateCheckout_nullUserId_returnsInvalid() {
        LoanApplicationValidationResponse r = LoanApplicationServiceImpl.validateCheckout(null);
        assertFalse(r.isValid());
    }

    @Test
    void validateCheckout_withUserId_returnsValid() {
        LoanApplicationValidationResponse r = LoanApplicationServiceImpl.validateCheckout(1L);
        assertTrue(r.isValid());
    }
}
