package com.payr.user_service.service;

import com.payr.user_service.dto.UserCreateFromAuthRequest;
import com.payr.user_service.dto.UserCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateFromAuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole(Role.CUSTOMER);
        testUser.setIsActive(true);

        authRequest = new UserCreateFromAuthRequest();
        authRequest.setId(1L);
        authRequest.setFirstName("John");
        authRequest.setLastName("Doe");
        authRequest.setEmail("john.doe@example.com");
        authRequest.setRole(Role.CUSTOMER);
    }

    @Test
    void createUser_ShouldThrowException() {
        log.info("Testing createUser: Should Throw Exception scenario");
        UserCreateRequest req = new UserCreateRequest();
        req.setEmail("test@example.com");
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(req);
        });
        
        assertEquals("Use auth-service registration. Manual /api/users create is disabled.", exception.getMessage());
        log.info("Finished testing createUser: Should Throw Exception scenario");
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        UserCreateRequest req = new UserCreateRequest();
        req.setEmail("test@example.com");
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(req);
        });
        
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void createUserFromAuth_Success() {
        log.info("Testing createUserFromAuth: Success scenario");
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService.createUserFromAuth(authRequest);

        assertNotNull(savedUser);
        assertEquals("john.doe@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        log.info("Finished testing createUserFromAuth: Success scenario");
    }

    @Test
    void createUserFromAuth_NullId_ShouldThrowException() {
        authRequest.setId(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUserFromAuth(authRequest);
        });

        assertEquals("id is required", exception.getMessage());
    }

    @Test
    void createUserFromAuth_IdAlreadyExists_ShouldReturnExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User user = userService.createUserFromAuth(authRequest);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserFromAuth_EmailAlreadyExists_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUserFromAuth(authRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void getUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUser(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
    }

    @Test
    void getUser_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUser(1L);
        });
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void setActive_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.setActive(1L, false);

        assertNotNull(updatedUser);
        assertFalse(updatedUser.getIsActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void assertRole_Success() {
        assertDoesNotThrow(() -> userService.assertRole(testUser, Role.CUSTOMER));
    }

    @Test
    void assertRole_Failure() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.assertRole(testUser, Role.ADMIN);
        });

        assertEquals("User role must be ADMIN to create this profile", exception.getMessage());
    }
}
