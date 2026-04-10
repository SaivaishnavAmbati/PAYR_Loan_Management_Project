package com.payr.loan_service.controller;

import com.payr.loan_service.config.JwtUtil;
import com.payr.loan_service.dto.LoanApprovalResponseDto;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.service.LoanApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanApprovalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtUtil.class)
class LoanApprovalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanApplicationService loanApplicationService;

    private LoanApprovalResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new LoanApprovalResponseDto(1, LoanStatus.APPROVED, "Approved");
    }

    @Test
    void approveLoan_Success() throws Exception {
        when(loanApplicationService.approveLoan(1)).thenReturn(responseDto);

        mockMvc.perform(post("/api/loans/loanOfficer/approve/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(loanApplicationService, times(1)).approveLoan(1);
    }

    @Test
    void rejectLoan_Success() throws Exception {
        LoanApprovalResponseDto rejectResponse = new LoanApprovalResponseDto(1, LoanStatus.REJECTED, "Rejected");
        when(loanApplicationService.rejectLoan(1)).thenReturn(rejectResponse);

        mockMvc.perform(post("/api/loans/loanOfficer/reject/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(1))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(loanApplicationService, times(1)).rejectLoan(1);
    }
}
