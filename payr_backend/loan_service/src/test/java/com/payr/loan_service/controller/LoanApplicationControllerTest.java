package com.payr.loan_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.loan_service.dto.LoanApplyRequestDto;
import com.payr.loan_service.dto.LoanApplyResponseDto;
import com.payr.loan_service.service.LoanApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanApplyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new LoanApplyResponseDto();
        responseDto.setLoanId(1);
    }

    @Test
    void applyLoan_Success() throws Exception {
        when(loanApplicationService.applyLoan(any(LoanApplyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/loanApplication/apply")
                        .param("userId", "1")
                        .param("loanTypeId", "1")
                        .param("requestedAmount", "50000")
                        .param("tenureMonths", "24")
                        .param("emiDueDay", "5")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loanId").value(1));

        verify(loanApplicationService, times(1)).applyLoan(any(LoanApplyRequestDto.class));
    }

    @Test
    void getAllLoans_Success() throws Exception {
        when(loanApplicationService.getAllAppliedLoans()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/loanApplication/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1));

        verify(loanApplicationService, times(1)).getAllAppliedLoans();
    }

    @Test
    void getLoansByUserId_Success() throws Exception {
        when(loanApplicationService.getLoansByUserId(1L)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/loanApplication/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1));

        verify(loanApplicationService, times(1)).getLoansByUserId(1L);
    }
}
