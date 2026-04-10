package com.payr.loan_service.service.feign;


import com.payr.loan_service.dto.feignDto.NotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

//@FeignClient(name = "NOTIFICATION-SERVICE")
//public interface NotificationClient {
//
//    @PostMapping("/notifications/send")
//    void sendNotification(NotificationRequestDto request);
//}
