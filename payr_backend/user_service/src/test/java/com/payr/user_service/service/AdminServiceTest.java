package com.payr.user_service.service;

import com.payr.user_service.dto.AdminCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Admin;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.AdminRepository;
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
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private AdminCreateRequest req;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(Role.ADMIN);

        req = new AdminCreateRequest();
        req.setAdminName("Super Admin");
    }

    @Test
    void createAdminProfile_Success() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.ADMIN);
        when(adminRepository.existsById(1L)).thenReturn(false);
        
        Admin savedAdmin = new Admin();
        savedAdmin.setUser(testUser);
        savedAdmin.setAdminName("Super Admin");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin admin = adminService.createAdminProfile(1L, req);

        assertNotNull(admin);
        assertEquals("Super Admin", admin.getAdminName());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void createAdminProfile_AlreadyExists_ShouldThrowException() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.ADMIN);
        when(adminRepository.existsById(1L)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            adminService.createAdminProfile(1L, req);
        });

        assertEquals("Admin profile already exists for this user", exception.getMessage());
    }

    @Test
    void getAdmin_Success() {
        Admin admin = new Admin();
        admin.setUser(testUser);
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        Admin foundAdmin = adminService.getAdmin(1L);

        assertNotNull(foundAdmin);
    }

    @Test
    void getAdmin_NotFound_ShouldThrowException() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getAdmin(1L);
        });
    }
}
