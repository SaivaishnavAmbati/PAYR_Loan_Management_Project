package com.cartigo.authservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otp")
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 190)
    private String email;

    @Column(name = "otp_hash", nullable=false, length = 200)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length = 30)
    private OtpPurpose purpose;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    // Used only for REGISTER flow (temporary until OTP verified)
    @Column(name="temp_password_hash", length = 200)
    private String tempPasswordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="temp_role", length = 20)
    private Role tempRole;

    @Column(name="temp_first_name", length = 100)
    private String tempFirstName;

    @Column(name="temp_last_name", length = 100)
    private String tempLastName;

    public OtpEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }

    public OtpPurpose getPurpose() { return purpose; }
    public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }

    public String getTempPasswordHash() { return tempPasswordHash; }
    public void setTempPasswordHash(String tempPasswordHash) { this.tempPasswordHash = tempPasswordHash; }

    public Role getTempRole() { return tempRole; }
    public void setTempRole(Role tempRole) { this.tempRole = tempRole; }

    public String getTempFirstName() { return tempFirstName; }
    public void setTempFirstName(String tempFirstName) { this.tempFirstName = tempFirstName; }

    public String getTempLastName() { return tempLastName; }
    public void setTempLastName(String tempLastName) { this.tempLastName = tempLastName; }
}
