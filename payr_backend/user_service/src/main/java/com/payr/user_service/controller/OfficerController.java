package com.payr.user_service.controller;


import com.payr.user_service.dto.OfficerCreateRequest;
import com.payr.user_service.model.Officer;
import com.payr.user_service.service.OfficerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officers")
public class OfficerController {

    private final OfficerService officerService;

    public OfficerController(OfficerService officerService) {
        this.officerService = officerService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Officer> create(@PathVariable Long userId,
                                          @Valid @RequestBody OfficerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(officerService.createOfficerProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Officer> get(@PathVariable Long userId) {
        return ResponseEntity.ok(officerService.getOfficer(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Officer> update(@PathVariable Long userId,
                                          @Valid @RequestBody OfficerCreateRequest req) {
        return ResponseEntity.ok(officerService.updateOfficer(userId, req));
    }

    // Admin can activate/deactivate officer
    @PatchMapping("/{userId}/active")
    public ResponseEntity<Officer> setActive(@PathVariable Long userId,
                                             @RequestParam boolean value) {
        return ResponseEntity.ok(officerService.setActive(userId, value));
    }
}
