package com.payr.user_service.repository;


import com.payr.user_service.model.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficerRepository extends JpaRepository<Officer, Long> {

    boolean existsByEmployeeId(String employeeId);

    Optional<Officer> findByEmployeeId(String employeeId);
}
