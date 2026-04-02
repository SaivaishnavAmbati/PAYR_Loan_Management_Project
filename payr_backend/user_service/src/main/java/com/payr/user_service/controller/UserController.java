package com.payr.user_service.controller;



import com.payr.user_service.dto.UserCreateFromAuthRequest;
import com.payr.user_service.dto.UserResponse;
import com.payr.user_service.mapper.UserMapper;
import com.payr.user_service.model.User;
import com.payr.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/from-auth")
    public ResponseEntity<UserResponse> createFromAuth(@Valid @RequestBody UserCreateFromAuthRequest req) {
        User created = userService.createUserFromAuth(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.getUser(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<UserResponse> setActive(@PathVariable Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.setActive(id, value)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

