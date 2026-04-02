package com.payr.user_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OfficerCreateRequest {

    @NotBlank
    @Size(max = 50)
    private String employeeId;

    @NotBlank
    @Size(max = 100)
    private String department;

    @NotBlank
    @Size(max = 100)
    private String designation;

    public OfficerCreateRequest() {}

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
}
