package com.payr.authservice.controller;


import com.payr.authservice.dto.*;
import com.payr.authservice.entity.OtpEntity;
import com.payr.authservice.entity.OtpPurpose;
import com.payr.authservice.repository.UserRepository;
import com.payr.authservice.security.JwtUtil;
import com.payr.authservice.service.AuthService;
import com.payr.authservice.service.EmailService;
import com.payr.authservice.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(OtpService otpService,
                          EmailService emailService,
                          AuthService authService,
                          UserRepository userRepository,JwtUtil jwtUtil) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil=jwtUtil;
    }

    // REGISTER via OTP
    @PostMapping("/register/send-otp")
    public String sendRegisterOtp(@Valid @RequestBody RegisterRequest req) {

        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        String otp = otpService.generateAndSaveOtpForRegister(
                email,
                req.getFirstName(),
                req.getLastName(),
                req.getPassword(),
                req.getRole()
        );

        emailService.sendOtpMail(email, otp, "REGISTER");
        return "OTP sent to email for registration";
    }

    @PostMapping("/register/verify-otp")
    public AuthResponse verifyRegisterOtp(@Valid @RequestBody OtpVerifyRequest req) {

        String email = req.getEmail().trim().toLowerCase();

        Optional<OtpEntity> record = otpService.verifyOtp(email, req.getOtp(), OtpPurpose.REGISTER);
        if (record.isEmpty()) {
            throw new IllegalArgumentException("Invalid / Expired OTP");
        }

        // Create AUTH user + create USER-SERVICE profile
        authService.registerUserAndCreateProfile(record.get());

        return authService.issueTokenForEmail(email);
    }

    // LOGIN via OTP
    @PostMapping("/login/send-otp")
    public String sendLoginOtp(@Valid @RequestBody EmailRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        String otp = otpService.generateAndSaveOtpForLogin(email);
        emailService.sendOtpMail(email, otp, "LOGIN");
        return "OTP sent to email for login";
    }

    @PostMapping("/login/verify-otp")
    public AuthResponse verifyLoginOtp(@Valid @RequestBody OtpVerifyRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        Optional<OtpEntity> record = otpService.verifyOtp(email, req.getOtp(), OtpPurpose.LOGIN);
        if (record.isEmpty()) {
            throw new IllegalArgumentException("Invalid / Expired OTP");
        }

        return authService.issueTokenForEmail(email);
    }

    // LOGIN via PASSWORD
    @PostMapping("/login/password")
    public AuthResponse loginPassword(@Valid @RequestBody LoginRequest req) {
        req.setEmail(req.getEmail() == null ? null : req.getEmail().trim().toLowerCase());
        return authService.loginWithPassword(req);
    }

}

