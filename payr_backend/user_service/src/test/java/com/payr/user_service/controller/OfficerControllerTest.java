package com.payr.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.user_service.dto.OfficerCreateRequest;
import com.payr.user_service.model.Officer;
import com.payr.user_service.model.User;
import com.payr.user_service.service.OfficerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficerController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfficerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfficerService officerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Officer testOfficer;
    private OfficerCreateRequest req;

    @BeforeEach
    void setUp() {
        testOfficer = new Officer();
        testOfficer.setEmployeeId("EMP001");
        testOfficer.setDepartment("Loans");
        testOfficer.setDesignation("Senior Officer");
        testOfficer.setIsActive(true);

        User user = new User();
        user.setId(1L);
        testOfficer.setUser(user);

        req = new OfficerCreateRequest();
        req.setEmployeeId("EMP001");
        req.setDepartment("Loans");
        req.setDesignation("Senior Officer");
    }

    @Test
    void create_Success() throws Exception {
        when(officerService.createOfficerProfile(eq(1L), any(OfficerCreateRequest.class)))
                .thenReturn(testOfficer);

        mockMvc.perform(post("/api/officers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.department").value("Loans"))
                .andExpect(jsonPath("$.designation").value("Senior Officer"));

        verify(officerService, times(1)).createOfficerProfile(eq(1L), any(OfficerCreateRequest.class));
    }

    @Test
    void get_Success() throws Exception {
        when(officerService.getOfficer(1L)).thenReturn(testOfficer);

        mockMvc.perform(get("/api/officers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.department").value("Loans"))
                .andExpect(jsonPath("$.designation").value("Senior Officer"));

        verify(officerService, times(1)).getOfficer(1L);
    }

    @Test
    void update_Success() throws Exception {
        when(officerService.updateOfficer(eq(1L), any(OfficerCreateRequest.class)))
                .thenReturn(testOfficer);

        mockMvc.perform(put("/api/officers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.department").value("Loans"))
                .andExpect(jsonPath("$.designation").value("Senior Officer"));

        verify(officerService, times(1)).updateOfficer(eq(1L), any(OfficerCreateRequest.class));
    }

    @Test
    void setActive_Success() throws Exception {
        Officer inactiveOfficer = new Officer();
        inactiveOfficer.setIsActive(false);

        when(officerService.setActive(1L, false)).thenReturn(inactiveOfficer);

        mockMvc.perform(patch("/api/officers/1/active")
                        .param("value", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(officerService, times(1)).setActive(1L, false);
    }
}