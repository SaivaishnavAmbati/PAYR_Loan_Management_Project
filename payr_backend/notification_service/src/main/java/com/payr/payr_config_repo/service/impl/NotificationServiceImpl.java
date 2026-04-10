package com.payr.payr_config_repo.service.impl;

import com.payr.payr_config_repo.dto.NotificationRequest;
import com.payr.payr_config_repo.model.NotificationLog;
import com.payr.payr_config_repo.repository.NotificationRepository;
import com.payr.payr_config_repo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository repository;

    public void sendEmail(NotificationRequest request) {

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(request.getEmail());
            mail.setSubject(request.getSubject());
            mail.setText(request.getMessage());

            mailSender.send(mail);

            repository.save(
                    NotificationLog.builder()
                            .userId(request.getUserId())
                            .email(request.getEmail())
                            .notificationType(request.getNotificationType())
                            .message(request.getMessage())
                            .status("SENT")
                            .sentAt(LocalDateTime.now())
                            .build()
            );

            log.info("Email sent successfully to {}", request.getEmail());

        } catch (Exception ex) {

            repository.save(
                    NotificationLog.builder()
                            .userId(request.getUserId())
                            .email(request.getEmail())
                            .notificationType(request.getNotificationType())
                            .message(request.getMessage())
                            .status("FAILED")
                            .sentAt(LocalDateTime.now())
                            .build()
            );

            log.error("Email sending failed", ex);
            throw new RuntimeException("Email sending failed");
        }
    }
}
