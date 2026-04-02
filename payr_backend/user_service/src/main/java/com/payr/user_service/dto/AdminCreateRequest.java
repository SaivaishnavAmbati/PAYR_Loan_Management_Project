package com.payr.user_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String adminName;

    public AdminCreateRequest() {}

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}

