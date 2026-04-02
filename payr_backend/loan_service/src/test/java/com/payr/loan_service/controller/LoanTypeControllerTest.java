package com.payr.loan_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.loan_service.dto.LoanTypeRequestDto;
import com.payr.loan_service.dto.LoanTypeResponseDto;
import com.payr.loan_service.service.LoanTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanTypeService loanTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanTypeRequestDto requestDto;
    private LoanTypeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Set all required fields to pass validation
        requestDto = new LoanTypeRequestDto();
        requestDto.setName("Personal Loan");
        requestDto.setInterestRate(12.5);
        requestDto.setMinAmount(BigDecimal.valueOf(1000));
        requestDto.setMaxAmount(BigDecimal.valueOf(5000));
        requestDto.setMinTenureMonths(6);
        requestDto.setMaxTenureMonths(24);

        responseDto = new LoanTypeResponseDto();
        responseDto.setId(1);
        responseDto.setName("Personal Loan");
    }

    @Test
    void createLoanType_Success() throws Exception {
        when(loanTypeService.createLoanType(any(LoanTypeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/admin/loanTypes/createLoanType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Personal Loan"));

        verify(loanTypeService, times(1)).createLoanType(any(LoanTypeRequestDto.class));
    }

    @Test
    void getActiveLoanTypes_Success() throws Exception {
        when(loanTypeService.getAllActiveLoanTypes()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/admin/loanTypes/getLoans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Personal Loan"));

        verify(loanTypeService, times(1)).getAllActiveLoanTypes();
    }

    @Test
    void getLoanTypeById_Success() throws Exception {
        when(loanTypeService.getLoanTypeById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/admin/loanTypes/getLoanById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Personal Loan"));

        verify(loanTypeService, times(1)).getLoanTypeById(1);
    }
}