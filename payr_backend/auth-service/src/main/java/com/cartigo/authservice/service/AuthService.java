package com.cartigo.authservice.service;

import com.cartigo.authservice.client.UserServiceClient;
import com.cartigo.authservice.client.dto.UserCreateFromAuthRequest;
import com.cartigo.authservice.dto.AuthResponse;
import com.cartigo.authservice.dto.LoginRequest;
import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.Role;
import com.cartigo.authservice.entity.User;
import com.cartigo.authservice.repository.UserRepository;
import com.cartigo.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceClient userServiceClient;

    @Value("${cartigo.user-service.enabled:true}")
    private boolean userServiceEnabled;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserServiceClient userServiceClient) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Called after REGISTER OTP is verified.
     * 1) Creates auth-user (email/passwordHash/role)
     * 2) Creates user-service profile with SAME userId (critical)
     */
    public void registerUserAndCreateProfile(OtpEntity otpRecord) {
        String email = otpRecord.getEmail().trim().toLowerCase();
        Role role = otpRecord.getTempRole() == null ? Role.CUSTOMER : otpRecord.getTempRole();

        if (userRepository.existsByEmail(email)) {
            return; // already registered
        }

        User u = new User();
        u.setEmail(email);
        u.setPassword(otpRecord.getTempPasswordHash());
        u.setRole(role);

        User saved = userRepository.save(u);
        Long authUserId = saved.getId(); // ✅ THIS is the id we must keep everywhere

        if (userServiceEnabled) {
            try {
                userServiceClient.createUserFromAuth(new UserCreateFromAuthRequest(
                        authUserId,
                        otpRecord.getTempFirstName(),
                        otpRecord.getTempLastName(),
                        email,
                        role.name()
                ));
            } catch (Exception ignored) {
                // signup still succeeds even if user-service is temporarily down
            }
        }
    }

    public AuthResponse loginWithPassword(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        if (!auth.isAuthenticated()) {
            throw new RuntimeException("Invalid login");
        }

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
    }

    public AuthResponse issueTokenForEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
    }
}
