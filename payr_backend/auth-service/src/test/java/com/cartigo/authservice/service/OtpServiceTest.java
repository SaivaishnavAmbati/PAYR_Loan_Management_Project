package com.cartigo.authservice.service;

import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.OtpPurpose;
import com.cartigo.authservice.entity.Role;
import com.cartigo.authservice.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OtpService otpService;

    private OtpEntity otpEntity;

    @BeforeEach
    void setUp() {
        otpEntity = new OtpEntity();
        otpEntity.setId(1L);
        otpEntity.setEmail("test@example.com");
        otpEntity.setOtpHash("encodedOtp");
        otpEntity.setPurpose(OtpPurpose.REGISTER);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpEntity.setAttemptCount(0);
    }

    @Test
    void generateAndSaveOtpForRegister_Success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedValue");

        String otp = otpService.generateAndSaveOtpForRegister("test@example.com", "John", "Doe", "password", Role.CUSTOMER);

        assertNotNull(otp);
        assertTrue(otp.matches("\\d{6}")); // 6 digit OTP
        verify(otpRepository, times(1)).deleteByEmailAndPurpose("test@example.com", OtpPurpose.REGISTER);
        verify(otpRepository, times(1)).save(any(OtpEntity.class));
    }

    @Test
    void generateAndSaveOtpForLogin_Success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedValue");

        String otp = otpService.generateAndSaveOtpForLogin("test@example.com");

        assertNotNull(otp);
        assertTrue(otp.matches("\\d{6}")); // 6 digit OTP
        verify(otpRepository, times(1)).deleteByEmailAndPurpose("test@example.com", OtpPurpose.LOGIN);
        verify(otpRepository, times(1)).save(any(OtpEntity.class));
    }

    @Test
    void verifyOtp_Success() {
        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc("test@example.com", OtpPurpose.REGISTER))
                .thenReturn(Optional.of(otpEntity));
        when(passwordEncoder.matches("123456", "encodedOtp")).thenReturn(true);

        Optional<OtpEntity> result = otpService.verifyOtp("test@example.com", "123456", OtpPurpose.REGISTER);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(otpRepository, times(1)).delete(otpEntity);
    }

    @Test
    void verifyOtp_Expired_ReturnsEmpty() {
        otpEntity.setExpiryTime(LocalDateTime.now().minusMinutes(1));
        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc("test@example.com", OtpPurpose.REGISTER))
                .thenReturn(Optional.of(otpEntity));

        Optional<OtpEntity> result = otpService.verifyOtp("test@example.com", "123456", OtpPurpose.REGISTER);

        assertTrue(result.isEmpty());
        verify(otpRepository, times(1)).delete(otpEntity);
    }

    @Test
    void verifyOtp_WrongOtp_IncrementsAttemptCount() {
        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc("test@example.com", OtpPurpose.REGISTER))
                .thenReturn(Optional.of(otpEntity));
        when(passwordEncoder.matches("wrongOtp", "encodedOtp")).thenReturn(false);

        Optional<OtpEntity> result = otpService.verifyOtp("test@example.com", "wrongOtp", OtpPurpose.REGISTER);

        assertTrue(result.isEmpty());
        assertEquals(1, otpEntity.getAttemptCount());
        verify(otpRepository, times(1)).save(otpEntity);
    }

    @Test
    void verifyOtp_MaxAttemptsReached_DeletesRecord() {
        otpEntity.setAttemptCount(4);
        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc("test@example.com", OtpPurpose.REGISTER))
                .thenReturn(Optional.of(otpEntity));
        when(passwordEncoder.matches("wrongOtp", "encodedOtp")).thenReturn(false);

        Optional<OtpEntity> result = otpService.verifyOtp("test@example.com", "wrongOtp", OtpPurpose.REGISTER);

        assertTrue(result.isEmpty());
        assertEquals(5, otpEntity.getAttemptCount());
        verify(otpRepository, times(1)).save(otpEntity);
        verify(otpRepository, times(1)).delete(otpEntity);
    }
}
