package com.payr.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.user_service.dto.AdminCreateRequest;
import com.payr.user_service.model.Admin;
import com.payr.user_service.model.User;
import com.payr.user_service.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private Admin testAdmin;
    private AdminCreateRequest req;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setAdminName("Super Admin");

        User user = new User();
        user.setId(1L);
        testAdmin.setUser(user);

        req = new AdminCreateRequest();
        req.setAdminName("Super Admin");
    }

    @Test
    void create_Success() throws Exception {
        when(adminService.createAdminProfile(eq(1L), any(AdminCreateRequest.class))).thenReturn(testAdmin);

        mockMvc.perform(post("/api/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adminName").value("Super Admin"));

        verify(adminService, times(1)).createAdminProfile(eq(1L), any(AdminCreateRequest.class));
    }

    @Test
    void get_Success() throws Exception {
        when(adminService.getAdmin(1L)).thenReturn(testAdmin);

        mockMvc.perform(get("/api/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminName").value("Super Admin"));

        verify(adminService, times(1)).getAdmin(1L);
    }
}
