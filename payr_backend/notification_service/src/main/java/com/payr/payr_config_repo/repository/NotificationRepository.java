package com.payr.payr_config_repo.repository;

import com.payr.payr_config_repo.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {

}