package com.payr.loan_service.service;


import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import org.apache.coyote.BadRequestException;

import java.util.List;

/**
 * Service interface for LoanType operations.
 * Defines the contract for creating, fetching, and validating loan types.
 */
public interface LoanTypeService {

    /**
     * Create a new loan type.
     * request DTO containing loan type details
     * LoanTypeResponseDTO mapped from the saved entity
     */
    LoanTypeResponseDto createLoanType(LoanTypeRequestDto request);

    /**
     * Get all active loan types.
     *  List of LoanTypeResponseDTO
     */
    List<LoanTypeResponseDto> getAllActiveLoanTypes();

    /**
     * Get a loan type by its ID.
     * ID of the loan type
     *  LoanTypeResponseDTO mapped from the entity
     */
    LoanTypeResponseDto getLoanTypeById(Integer id);
}

