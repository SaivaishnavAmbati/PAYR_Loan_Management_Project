package com.payr.authservice.service;

import com.payr.authservice.client.UserServiceClient;
import com.payr.authservice.dto.AuthResponse;
import com.payr.authservice.dto.LoginRequest;
import com.payr.authservice.entity.OtpEntity;
import com.payr.authservice.entity.Role;
import com.payr.authservice.entity.User;
import com.payr.authservice.repository.UserRepository;
import com.payr.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private OtpEntity otpEntity;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setRole(Role.CUSTOMER);

        otpEntity = new OtpEntity();
        otpEntity.setEmail("test@example.com");
        otpEntity.setTempRole(Role.CUSTOMER);
        otpEntity.setTempFirstName("John");
        otpEntity.setTempLastName("Doe");
        otpEntity.setTempPasswordHash("hashedPassword");
    }

    @Test
    void registerUserAndCreateProfile_NewUser_Success() {
        log.info("Testing registerUserAndCreateProfile: New User Success scenario");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Use reflection or Spring ReflectionTestUtils if needed field injection, but default for boolean is false.
        // Assuming userServiceEnabled is false initially for mock, or we just verify it doesn't crash if false.

        authService.registerUserAndCreateProfile(otpEntity);

        verify(userRepository, times(1)).save(any(User.class));
        log.info("Finished testing registerUserAndCreateProfile: New User Success scenario");
    }

    @Test
    void registerUserAndCreateProfile_UserAlreadyExists_DoesNothing() {
        log.info("Testing registerUserAndCreateProfile: User Already Exists scenario");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        authService.registerUserAndCreateProfile(otpEntity);

        verify(userRepository, never()).save(any(User.class));
        log.info("Finished testing registerUserAndCreateProfile: User Already Exists scenario");
    }

    @Test
    void loginWithPassword_Success() {
        log.info("Testing loginWithPassword: Success scenario");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(1L, "test@example.com", Role.CUSTOMER)).thenReturn("dummy-token");

        AuthResponse response = authService.loginWithPassword(loginRequest);

        assertNotNull(response);
        assertEquals("dummy-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        log.info("Finished testing loginWithPassword: Success scenario");
    }

    @Test
    void loginWithPassword_InvalidLogin_ShouldThrowException() {
        log.info("Testing loginWithPassword: Invalid Login scenario");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong-password");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        assertThrows(RuntimeException.class, () -> authService.loginWithPassword(loginRequest));
        log.info("Finished testing loginWithPassword: Invalid Login scenario");
    }

    @Test
    void issueTokenForEmail_Success() {
        log.info("Testing issueTokenForEmail: Success scenario");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(1L, "test@example.com", Role.CUSTOMER)).thenReturn("dummy-token");

        AuthResponse response = authService.issueTokenForEmail("test@example.com");

        assertNotNull(response);
        assertEquals("dummy-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        log.info("Finished testing issueTokenForEmail: Success scenario");
    }

    @Test
    void issueTokenForEmail_UserNotFound_ShouldThrowException() {
        log.info("Testing issueTokenForEmail: User Not Found scenario");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.issueTokenForEmail("notfound@example.com");
        });

        assertEquals("User not found: notfound@example.com", exception.getMessage());
        log.info("Finished testing issueTokenForEmail: User Not Found scenario");
    }
}
