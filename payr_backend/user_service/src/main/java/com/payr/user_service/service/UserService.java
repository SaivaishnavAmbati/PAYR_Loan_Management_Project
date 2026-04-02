package com.payr.user_service.service;



import com.payr.user_service.dto.UserCreateFromAuthRequest;
import com.payr.user_service.dto.UserCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class    UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Manual create (optional)
    public User createUser(UserCreateRequest req) {
        String normalizedEmail = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        // ❗ For manual create you must generate id in another way OR disallow this endpoint.
        // Because now id is not auto-generated.
        throw new BadRequestException("Use auth-service registration. Manual /api/users create is disabled.");
    }

    // ✅ create using id from auth-service
    public User createUserFromAuth(UserCreateFromAuthRequest req) {
        if (req.getId() == null) {
            throw new BadRequestException("id is required");
        }

        String normalizedEmail = req.getEmail().trim().toLowerCase();

        // if id already exists, return it (idempotent)
        if (userRepository.existsById(req.getId())) {
            return getUser(req.getId());
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        User u = new User();
        u.setId(req.getId()); // ✅ critical
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(normalizedEmail);
        u.setRole(req.getRole());
        u.setIsActive(true);

        return userRepository.save(u);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User setActive(Long id, boolean active) {
        User u = getUser(id);
        u.setIsActive(active);
        return userRepository.save(u);
    }

    public void deleteUser(Long id) {
        User u = getUser(id);
        userRepository.delete(u);
    }

    public void assertRole(User user, Role expected) {
        if (user.getRole() != expected) {
            throw new BadRequestException("User role must be " + expected + " to create this profile");
        }
    }
}
