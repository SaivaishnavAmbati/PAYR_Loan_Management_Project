package com.payr.payr_config_repo.service;


import com.payr.payr_config_repo.dto.NotificationRequest;

public interface NotificationService{
    public void sendEmail(NotificationRequest request);
}