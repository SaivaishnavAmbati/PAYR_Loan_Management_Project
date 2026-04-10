package com.payr.user_service.service;

import com.payr.user_service.dto.OfficerCreateRequest;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.model.Officer;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.repository.OfficerRepository;
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
class OfficerServiceTest {

    @Mock
    private OfficerRepository officerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OfficerService officerService;

    private User testUser;
    private OfficerCreateRequest req;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(Role.OFFICER);

        req = new OfficerCreateRequest();
        req.setEmployeeId("EMP001");
        req.setDepartment("Loans");
    }

    @Test
    void createOfficerProfile_Success() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.OFFICER);
        when(officerRepository.existsById(1L)).thenReturn(false);
        when(officerRepository.existsByEmployeeId("EMP001")).thenReturn(false);

        Officer savedOfficer = new Officer();
        savedOfficer.setUser(testUser);
        savedOfficer.setEmployeeId("EMP001");
        when(officerRepository.save(any(Officer.class))).thenReturn(savedOfficer);

        Officer officer = officerService.createOfficerProfile(1L, req);

        assertNotNull(officer);
        assertEquals("EMP001", officer.getEmployeeId());
        verify(officerRepository, times(1)).save(any(Officer.class));
    }

    @Test
    void createOfficerProfile_AlreadyExists_ShouldThrowException() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.OFFICER);
        when(officerRepository.existsById(1L)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            officerService.createOfficerProfile(1L, req);
        });

        assertEquals("Officer profile already exists for this user", exception.getMessage());
    }

    @Test
    void createOfficerProfile_EmployeeIdTaken_ShouldThrowException() {
        when(userService.getUser(1L)).thenReturn(testUser);
        doNothing().when(userService).assertRole(testUser, Role.OFFICER);
        when(officerRepository.existsById(1L)).thenReturn(false);
        when(officerRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            officerService.createOfficerProfile(1L, req);
        });

        assertEquals("Employee ID already exists", exception.getMessage());
    }

    @Test
    void getOfficer_Success() {
        Officer o = new Officer();
        o.setUser(testUser);
        when(officerRepository.findById(1L)).thenReturn(Optional.of(o));

        Officer found = officerService.getOfficer(1L);

        assertNotNull(found);
    }

    @Test
    void getOfficer_NotFound_ShouldThrowException() {
        when(officerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            officerService.getOfficer(1L);
        });
    }

    @Test
    void updateOfficer_Success() {
        Officer existing = new Officer();
        existing.setUser(testUser);
        existing.setDepartment("Old Dept");

        when(officerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(officerRepository.save(any(Officer.class))).thenReturn(existing);

        OfficerCreateRequest updateReq = new OfficerCreateRequest();
        updateReq.setDepartment("New Dept");

        Officer updated = officerService.updateOfficer(1L, updateReq);

        assertNotNull(updated);
        assertEquals("New Dept", existing.getDepartment());
        verify(officerRepository, times(1)).save(existing);
    }
    
    @Test
    void setActive_Success() {
        Officer existing = new Officer();
        existing.setUser(testUser);
        existing.setIsActive(true);
        
        when(officerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(officerRepository.save(any(Officer.class))).thenReturn(existing);
        
        Officer updated = officerService.setActive(1L, false);
        
        assertNotNull(updated);
        assertFalse(updated.getIsActive());
        verify(officerRepository, times(1)).save(existing);
    }
}
