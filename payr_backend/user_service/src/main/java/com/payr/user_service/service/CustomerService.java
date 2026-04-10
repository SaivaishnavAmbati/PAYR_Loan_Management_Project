package com.payr.user_service.service;



import com.payr.user_service.dto.CustomerCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Customer;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    public CustomerService(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    public Customer createCustomerProfile(Long userId, CustomerCreateRequest req) {
        User user = userService.getUser(userId);
        userService.assertRole(user, Role.CUSTOMER);

        if (customerRepository.existsById(userId)) {
            throw new BadRequestException("Customer profile already exists for this user");
        }

        Customer c = new Customer();
        c.setUser(user);
        c.setPhoneNumber(req.getPhoneNumber());
        c.setAddressLine1(req.getAddressLine1());
        c.setCity(req.getCity());
        c.setState(req.getState());
        c.setPincode(req.getPincode());
        c.setCountry(req.getCountry());

        return customerRepository.save(c);
    }

    public Customer getCustomer(Long userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for userId " + userId));
    }

    public Customer updateCustomer(Long userId, CustomerCreateRequest req) {
        Customer c = getCustomer(userId);

        if (req.getPhoneNumber() != null) c.setPhoneNumber(req.getPhoneNumber());
        if (req.getAddressLine1() != null) c.setAddressLine1(req.getAddressLine1());
        if (req.getCity() != null) c.setCity(req.getCity());
        if (req.getState() != null) c.setState(req.getState());
        if (req.getPincode() != null) c.setPincode(req.getPincode());
        if (req.getCountry() != null) c.setCountry(req.getCountry());

        return customerRepository.save(c);
    }
}

