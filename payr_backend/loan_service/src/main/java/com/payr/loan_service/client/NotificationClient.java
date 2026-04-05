package com.payr.loan_service.client;

import com.payr.loan_service.config.AppConfig;
import com.payr.loan_service.config.NotificationFeignConfig;
import com.payr.loan_service.dto.feignDto.NotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service",
        configuration = NotificationFeignConfig.class
)
public interface NotificationClient {
    @PostMapping("/notifications/send")
    void sendNotification(@RequestBody NotificationRequestDto request);
}
