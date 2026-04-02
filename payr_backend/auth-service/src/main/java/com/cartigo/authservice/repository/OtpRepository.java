package com.cartigo.authservice.repository;

import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findTopByEmailAndPurposeOrderByIdDesc(String email, OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
