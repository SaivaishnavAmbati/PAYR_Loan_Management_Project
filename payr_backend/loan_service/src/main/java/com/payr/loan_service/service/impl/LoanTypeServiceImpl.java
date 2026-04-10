package com.payr.loan_service.service.impl;

import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import com.payr.loan_service.model.LoanType;
import com.payr.loan_service.repository.LoanTypeRepository;
import com.payr.loan_service.service.LoanTypeService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LoanTypeService.
 * Handles all business logic for creating and fetching loan types.
 */
@Service
public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeRepository loanTypeRepository;
    private final ModelMapper modelMapper;

    public LoanTypeServiceImpl(LoanTypeRepository loanTypeRepository, ModelMapper modelMapper) {
        this.loanTypeRepository = loanTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a new loan type.
     *
     * @param request DTO containing loan type details
     * @return DTO of the saved loan type
     */
    @Override
    public LoanTypeResponseDto createLoanType(LoanTypeRequestDto request) {
        // Validate request
        validateLoanTypeRequest(request);

        // Check if loan type name already exists among active loan types
        boolean exists = loanTypeRepository.findByActiveTrue()
                .stream()
                .anyMatch(lt -> lt.getName().equalsIgnoreCase(request.getName()));

        if (exists) {
            throw new IllegalArgumentException("Loan type name already exists");
        }

        // Map DTO to entity using ModelMapper
        LoanType loanType = modelMapper.map(request, LoanType.class);

        // Save entity
        LoanType saved = loanTypeRepository.save(loanType);

        // Map entity back to DTO
        return modelMapper.map(saved, LoanTypeResponseDto.class);
    }

    /**
     * Get all active loan types.
     *
     * @return List of LoanTypeResponseDto
     */
    @Override
    public List<LoanTypeResponseDto> getAllActiveLoanTypes() {
        List<LoanType> activeLoanTypes = loanTypeRepository.findByActiveTrue();

        List<LoanTypeResponseDto> dtoList = new ArrayList<>();
        for (LoanType loanType : activeLoanTypes) {
            dtoList.add(modelMapper.map(loanType, LoanTypeResponseDto.class));
        }
        return dtoList;
    }

    /**
     * Get a loan type by ID.
     *
     * @param id ID of the loan type
     * @return LoanTypeResponseDto
     */
    @Override
    public LoanTypeResponseDto getLoanTypeById(Integer id) {
        LoanType loanType = loanTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan type not found with id: " + id));
        return modelMapper.map(loanType, LoanTypeResponseDto.class);
    }

    /**
     * Validate the loan type request.
     *
     * @param request DTO to validate
     */
    private void validateLoanTypeRequest(LoanTypeRequestDto request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Loan type name is required");
        }

        if (request.getInterestRate() == null || request.getInterestRate() <= 0) {
            throw new IllegalArgumentException("Interest rate must be greater than 0");
        }

        if (request.getMinTenureMonths() == null || request.getMinTenureMonths() <= 0) {
            throw new IllegalArgumentException("Minimum tenure must be greater than 0 months");
        }

        if (request.getMaxTenureMonths() == null || request.getMaxTenureMonths() <= 0) {
            throw new IllegalArgumentException("Maximum tenure must be greater than 0 months");
        }

        if (request.getMinTenureMonths() > request.getMaxTenureMonths()) {
            throw new IllegalArgumentException("Minimum tenure cannot be greater than maximum tenure");
        }

        if (request.getMinAmount() == null || request.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Minimum amount must be greater than 0");
        }

        if (request.getMaxAmount() == null || request.getMaxAmount().compareTo(request.getMinAmount()) < 0) {
            throw new IllegalArgumentException("Maximum amount must be greater than or equal to minimum amount");
        }

        if (request.getActive() == null) {
            request.setActive(true); // Default to active
        }
    }
}
