package com.payr.user_service.service;

import com.payr.user_service.dto.CustomerCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Customer;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerService customerService;

    private User testUser;
    private CustomerCreateRequest req;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(Role.CUSTOMER);

        req = new CustomerCreateRequest();
        req.setPhoneNumber("1234567890");
        req.setCity("Test City");
    }

    @Test
    void createCustomerProfile_Success() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.CUSTOMER);
        when(customerRepository.existsById(1L)).thenReturn(false);

        Customer savedCustomer = new Customer();
        savedCustomer.setUser(testUser);
        savedCustomer.setPhoneNumber("1234567890");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer customer = customerService.createCustomerProfile(1L, req);

        assertNotNull(customer);
        assertEquals("1234567890", customer.getPhoneNumber());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomerProfile_AlreadyExists_ShouldThrowException() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.CUSTOMER);
        when(customerRepository.existsById(1L)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            customerService.createCustomerProfile(1L, req);
        });

        assertEquals("Customer profile already exists for this user", exception.getMessage());
    }

    @Test
    void getCustomer_Success() {
        Customer c = new Customer();
        c.setUser(testUser);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Customer found = customerService.getCustomer(1L);

        assertNotNull(found);
    }

    @Test
    void getCustomer_NotFound_ShouldThrowException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomer(1L);
        });
    }

    @Test
    void updateCustomer_Success() {
        Customer existing = new Customer();
        existing.setUser(testUser);
        existing.setCity("Old City");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenReturn(existing);

        CustomerCreateRequest updateReq = new CustomerCreateRequest();
        updateReq.setCity("New City");

        Customer updated = customerService.updateCustomer(1L, updateReq);

        assertNotNull(updated);
        assertEquals("New City", existing.getCity());
        verify(customerRepository, times(1)).save(existing);
    }
}
