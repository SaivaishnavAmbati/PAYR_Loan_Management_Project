package com.payr.loan_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.loan_service.dto.LoanOfficerApplicationResponseDto;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.service.LoanApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanOfficerController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanOfficerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    private LoanOfficerApplicationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new LoanOfficerApplicationResponseDto();
        responseDto.setLoanId(1);
        responseDto.setStatus(LoanStatus.PENDING);
    }

    @Test
    void getApplicationsByStatus_Success() throws Exception {
        when(loanApplicationService.getApplicationsByStatus(LoanStatus.PENDING)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/loanOfficer/applications/applications")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(loanApplicationService, times(1)).getApplicationsByStatus(LoanStatus.PENDING);
    }

    @Test
    void getAllApplications_Success() throws Exception {
        when(loanApplicationService.getAllApplications()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/loanOfficer/applications/applications/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanId").value(1));

        verify(loanApplicationService, times(1)).getAllApplications();
    }
}
