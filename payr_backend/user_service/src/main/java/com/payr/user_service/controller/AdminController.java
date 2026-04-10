package com.payr.user_service.controller;



import com.payr.user_service.dto.AdminCreateRequest;
import com.payr.user_service.model.Admin;
import com.payr.user_service.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Admin> create(@PathVariable Long userId, @Valid @RequestBody AdminCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdminProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Admin> get(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getAdmin(userId));
    }
}

