package com.payr.loan_service.service.impl;

import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import com.payr.loan_service.model.LoanType;
import com.payr.loan_service.repository.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanTypeServiceImplTest {

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LoanTypeServiceImpl loanTypeService;

    private LoanTypeRequestDto requestDto;
    private LoanType loanType;
    private LoanTypeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new LoanTypeRequestDto();
        requestDto.setName("Personal Loan");
        requestDto.setInterestRate(10.5);
        requestDto.setMinTenureMonths(12);
        requestDto.setMaxTenureMonths(60);
        requestDto.setMinAmount(BigDecimal.valueOf(10000));
        requestDto.setMaxAmount(BigDecimal.valueOf(500000));
        requestDto.setActive(true);

        loanType = new LoanType();
        loanType.setId(1);
        loanType.setName("Personal Loan");
        loanType.setActive(true);

        responseDto = new LoanTypeResponseDto();
        responseDto.setId(1);
        responseDto.setName("Personal Loan");
    }

    @Test
    void createLoanType_Success() {
        when(loanTypeRepository.findByActiveTrue()).thenReturn(new ArrayList<>());
        when(modelMapper.map(requestDto, LoanType.class)).thenReturn(loanType);
        when(loanTypeRepository.save(loanType)).thenReturn(loanType);
        when(modelMapper.map(loanType, LoanTypeResponseDto.class)).thenReturn(responseDto);

        LoanTypeResponseDto result = loanTypeService.createLoanType(requestDto);

        assertNotNull(result);
        assertEquals("Personal Loan", result.getName());
        verify(loanTypeRepository, times(1)).save(loanType);
    }

    @Test
    void createLoanType_ExistingName_ShouldThrowException() {
        List<LoanType> activeTypes = new ArrayList<>();
        activeTypes.add(loanType);
        when(loanTypeRepository.findByActiveTrue()).thenReturn(activeTypes);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanTypeService.createLoanType(requestDto);
        });

        assertEquals("Loan type name already exists", exception.getMessage());
        verify(loanTypeRepository, never()).save(any(LoanType.class));
    }

    @Test
    void createLoanType_InvalidRequest_ShouldThrowException() {
        requestDto.setInterestRate(0.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanTypeService.createLoanType(requestDto);
        });

        assertEquals("Interest rate must be greater than 0", exception.getMessage());
    }

    @Test
    void getAllActiveLoanTypes_Success() {
        List<LoanType> activeTypes = new ArrayList<>();
        activeTypes.add(loanType);
        
        when(loanTypeRepository.findByActiveTrue()).thenReturn(activeTypes);
        when(modelMapper.map(any(LoanType.class), eq(LoanTypeResponseDto.class))).thenReturn(responseDto);

        List<LoanTypeResponseDto> result = loanTypeService.getAllActiveLoanTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanTypeRepository, times(1)).findByActiveTrue();
    }

    @Test
    void getLoanTypeById_Success() {
        when(loanTypeRepository.findById(1)).thenReturn(Optional.of(loanType));
        when(modelMapper.map(loanType, LoanTypeResponseDto.class)).thenReturn(responseDto);

        LoanTypeResponseDto result = loanTypeService.getLoanTypeById(1);

        assertNotNull(result);
        assertEquals("Personal Loan", result.getName());
    }

    @Test
    void getLoanTypeById_NotFound_ShouldThrowException() {
        when(loanTypeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            loanTypeService.getLoanTypeById(1);
        });
    }
}
