package com.payr.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.user_service.dto.UserCreateFromAuthRequest;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Spring Security for unit tests
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserCreateFromAuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(Role.CUSTOMER);
        testUser.setIsActive(true);

        authRequest = new UserCreateFromAuthRequest();
        authRequest.setId(1L);
        authRequest.setFirstName("John");
        authRequest.setLastName("Doe");
        authRequest.setEmail("john.doe@example.com");
        authRequest.setRole(Role.CUSTOMER);
    }

    @Test
    void createFromAuth_Success() throws Exception {
        when(userService.createUserFromAuth(any(UserCreateFromAuthRequest.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/from-auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userService, times(1)).createUserFromAuth(any(UserCreateFromAuthRequest.class));
    }

    @Test
    void get_Success() throws Exception {
        when(userService.getUser(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void getAll_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void setActive_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("john.doe@example.com");
        updatedUser.setIsActive(false);

        when(userService.setActive(1L, false)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1/active")
                        .param("value", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(userService, times(1)).setActive(1L, false);
    }

    @Test
    void delete_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}
