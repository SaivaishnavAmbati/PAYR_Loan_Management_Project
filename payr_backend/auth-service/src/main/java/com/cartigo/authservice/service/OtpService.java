package com.cartigo.authservice.service;

import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.OtpPurpose;
import com.cartigo.authservice.entity.Role;
import com.cartigo.authservice.repository.OtpRepository;
import com.cartigo.authservice.util.OtpGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private static final int OTP_EXP_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public OtpService(OtpRepository otpRepository, PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateAndSaveOtpForRegister(String email,
                                                String firstName,
                                                String lastName,
                                                String rawPassword,
                                                Role role) {
        otpRepository.deleteByEmailAndPurpose(email, OtpPurpose.REGISTER);

        String otp = OtpGenerator.generate6DigitOtp();

        OtpEntity entity = new OtpEntity();
        entity.setEmail(email);
        entity.setOtpHash(passwordEncoder.encode(otp));
        entity.setPurpose(OtpPurpose.REGISTER);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXP_MINUTES));
        entity.setAttemptCount(0);

        entity.setTempFirstName(firstName);
        entity.setTempLastName(lastName);
        entity.setTempPasswordHash(passwordEncoder.encode(rawPassword));
        entity.setTempRole(role);

        otpRepository.save(entity);
        return otp;
    }

    public String generateAndSaveOtpForLogin(String email) {
        otpRepository.deleteByEmailAndPurpose(email, OtpPurpose.LOGIN);

        String otp = OtpGenerator.generate6DigitOtp();

        OtpEntity entity = new OtpEntity();
        entity.setEmail(email);
        entity.setOtpHash(passwordEncoder.encode(otp));
        entity.setPurpose(OtpPurpose.LOGIN);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXP_MINUTES));
        entity.setAttemptCount(0);

        otpRepository.save(entity);
        return otp;
    }

    /**
     * Verifies OTP by comparing with stored otpHash. Does NOT store OTP in DB.
     * - If expired -> empty
     * - If wrong -> increments attemptCount, deletes record when max attempts reached
     * - If correct -> returns record and deletes it
     */
    public Optional<OtpEntity> verifyOtp(String email, String otp, OtpPurpose purpose) {
        Optional<OtpEntity> opt = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(email, purpose);
        if (opt.isEmpty()) return Optional.empty();

        OtpEntity record = opt.get();

        if (record.getExpiryTime() == null || record.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(record);
            return Optional.empty();
        }

        if (record.getAttemptCount() != null && record.getAttemptCount() >= MAX_ATTEMPTS) {
            otpRepository.delete(record);
            return Optional.empty();
        }

        boolean matches = passwordEncoder.matches(otp, record.getOtpHash());
        if (!matches) {
            int next = (record.getAttemptCount() == null ? 0 : record.getAttemptCount()) + 1;
            record.setAttemptCount(next);
            otpRepository.save(record);

            if (next >= MAX_ATTEMPTS) {
                otpRepository.delete(record);
            }
            return Optional.empty();
        }

        otpRepository.delete(record);
        return Optional.of(record);
    }
}
