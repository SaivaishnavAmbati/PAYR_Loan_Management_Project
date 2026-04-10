package com.payr.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.user_service.dto.CustomerCreateRequest;
import com.payr.user_service.model.Customer;
import com.payr.user_service.model.User;
import com.payr.user_service.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;
    private CustomerCreateRequest req;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setCity("Test City");
        testCustomer.setAddressLine1("123 Main Street");
        testCustomer.setState("Telangana");
        testCustomer.setPincode("532001");
        testCustomer.setCountry("India");

        User user = new User();
        user.setId(1L);
        testCustomer.setUser(user);

        // Populate request with all required fields to pass validation
        req = new CustomerCreateRequest();
        req.setPhoneNumber("1234567890");
        req.setCity("Test City");
        req.setAddressLine1("123 Main Street");
        req.setState("Telangana");
        req.setPincode("532001");
        req.setCountry("India");
    }

    @Test
    void create_Success() throws Exception {
        when(customerService.createCustomerProfile(eq(1L), any(CustomerCreateRequest.class))).thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.addressLine1").value("123 Main Street"))
                .andExpect(jsonPath("$.state").value("Telangana"))
                .andExpect(jsonPath("$.pincode").value("532001"))
                .andExpect(jsonPath("$.country").value("India"));

        verify(customerService, times(1)).createCustomerProfile(eq(1L), any(CustomerCreateRequest.class));
    }

    @Test
    void get_Success() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(testCustomer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.addressLine1").value("123 Main Street"))
                .andExpect(jsonPath("$.state").value("Telangana"))
                .andExpect(jsonPath("$.pincode").value("532001"))
                .andExpect(jsonPath("$.country").value("India"));

        verify(customerService, times(1)).getCustomer(1L);
    }

    @Test
    void update_Success() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(CustomerCreateRequest.class))).thenReturn(testCustomer);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.addressLine1").value("123 Main Street"))
                .andExpect(jsonPath("$.state").value("Telangana"))
                .andExpect(jsonPath("$.pincode").value("532001"))
                .andExpect(jsonPath("$.country").value("India"));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerCreateRequest.class));
    }
}