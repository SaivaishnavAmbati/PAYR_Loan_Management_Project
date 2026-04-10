package com.payr.user_service.service;


import com.payr.user_service.dto.AdminCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Admin;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    public AdminService(AdminRepository adminRepository, UserService userService) {
        this.adminRepository = adminRepository;
        this.userService = userService;
    }

    public Admin createAdminProfile(Long userId, AdminCreateRequest req) {
        User user = userService.getUser(userId);
        userService.assertRole(user, Role.ADMIN);

        if (adminRepository.existsById(userId)) {
            throw new BadRequestException("Admin profile already exists for this user");
        }

        Admin a = new Admin();
        a.setUser(user);
        a.setAdminName(req.getAdminName());
        return adminRepository.save(a);
    }

    public Admin getAdmin(Long userId) {
        return adminRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found for userId " + userId));
    }
}
