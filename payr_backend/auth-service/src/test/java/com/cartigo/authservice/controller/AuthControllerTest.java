package com.cartigo.authservice.controller;

import com.cartigo.authservice.security.JwtAuthFilter;
import com.cartigo.authservice.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cartigo.authservice.dto.*;
import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.OtpPurpose;
import com.cartigo.authservice.entity.Role;
import com.cartigo.authservice.repository.UserRepository;
import com.cartigo.authservice.service.AuthService;
import com.cartigo.authservice.service.EmailService;
import com.cartigo.authservice.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockBean
    private JwtUtil jwtUtil; // mock for test

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OtpService otpService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private OtpEntity otpEntity;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        otpEntity = new OtpEntity();
        otpEntity.setEmail("test@example.com");
        otpEntity.setOtpHash("123456");

        authResponse = new AuthResponse("dummy-token", 1L, "test@example.com", Role.CUSTOMER);
    }

    @Test
    void sendRegisterOtp_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password");
        request.setRole(Role.CUSTOMER);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(otpService.generateAndSaveOtpForRegister("test@example.com", "John", "Doe", "password", Role.CUSTOMER))
                .thenReturn("123456");
        doNothing().when(emailService).sendOtpMail("test@example.com", "123456", "REGISTER");

        mockMvc.perform(post("/auth/register/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to email for registration"));

        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(otpService, times(1)).generateAndSaveOtpForRegister("test@example.com", "John", "Doe", "password", Role.CUSTOMER);
    }

    @Test
    void verifyRegisterOtp_Success() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        when(otpService.verifyOtp("test@example.com", "123456", OtpPurpose.REGISTER))
                .thenReturn(Optional.of(otpEntity));
        doNothing().when(authService).registerUserAndCreateProfile(otpEntity);
        when(authService.issueTokenForEmail("test@example.com")).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(otpService, times(1)).verifyOtp("test@example.com", "123456", OtpPurpose.REGISTER);
        verify(authService, times(1)).issueTokenForEmail("test@example.com");
    }

    @Test
    void sendLoginOtp_Success() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(otpService.generateAndSaveOtpForLogin("test@example.com")).thenReturn("123456");
        doNothing().when(emailService).sendOtpMail("test@example.com", "123456", "LOGIN");

        mockMvc.perform(post("/auth/login/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to email for login"));

        verify(otpService, times(1)).generateAndSaveOtpForLogin("test@example.com");
    }

    @Test
    void verifyLoginOtp_Success() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        when(otpService.verifyOtp("test@example.com", "123456", OtpPurpose.LOGIN))
                .thenReturn(Optional.of(otpEntity));
        when(authService.issueTokenForEmail("test@example.com")).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(otpService, times(1)).verifyOtp("test@example.com", "123456", OtpPurpose.LOGIN);
    }

    @Test
    void loginPassword_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authService.loginWithPassword(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));

        verify(authService, times(1)).loginWithPassword(any(LoginRequest.class));
    }
}
