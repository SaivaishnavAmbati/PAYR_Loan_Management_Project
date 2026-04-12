package com.payr.payr_config_repo.service.impl;

import com.payr.payr_config_repo.dto.NotificationRequest;
import com.payr.payr_config_repo.model.NotificationLog;
import com.payr.payr_config_repo.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendEmail_Success() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setNotificationType("TEST");

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(repository.save(any(NotificationLog.class))).thenReturn(new NotificationLog());

        notificationService.sendEmail(request);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(repository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void sendEmail_Failure_SavesFailedLog() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setNotificationType("TEST");

        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () -> notificationService.sendEmail(request));

        verify(repository, times(1)).save(argThat(log -> "FAILED".equals(log.getStatus())));
    }
}
