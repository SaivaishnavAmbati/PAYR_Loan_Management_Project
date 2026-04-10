package com.payr.loan_service.repository;

import com.payr.loan_service.model.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository // Marks this interface as a Spring-managed repository bean
public interface LoanTypeRepository extends JpaRepository<LoanType, Integer> {

    List<LoanType> findByActiveTrue();
}
