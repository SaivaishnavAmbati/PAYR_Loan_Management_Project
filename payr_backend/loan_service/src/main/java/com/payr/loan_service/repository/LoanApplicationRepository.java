package com.payr.loan_service.repository;

import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Integer> {

    List<LoanApplication> findByUserId(Long userId);

    List<LoanApplication> findByStatus(LoanStatus status);
}
