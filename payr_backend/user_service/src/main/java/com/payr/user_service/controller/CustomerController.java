package com.payr.user_service.controller;

import com.payr.user_service.dto.CustomerCreateRequest;
import com.payr.user_service.model.Customer;
import com.payr.user_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Customer> create(@PathVariable Long userId, @Valid @RequestBody CustomerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomerProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Customer> get(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomer(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Customer> update(@PathVariable Long userId, @Valid @RequestBody CustomerCreateRequest req) {
        return ResponseEntity.ok(customerService.updateCustomer(userId, req));
    }
}

