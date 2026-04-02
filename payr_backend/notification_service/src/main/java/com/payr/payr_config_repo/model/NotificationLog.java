package com.payr.payr_config_repo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long notificationId;

        private Long userId;

        private String email;

        private String notificationType;

        private String message;

        private String status;

        private LocalDateTime sentAt;
    }
