package com.payr.user_service.service;


import com.payr.user_service.dto.OfficerCreateRequest;
import com.payr.user_service.model.Officer;
import com.payr.user_service.model.Role;
import com.payr.user_service.model.User;
import com.payr.user_service.exception.BadRequestException;
import com.payr.user_service.exception.ResourceNotFoundException;
import com.payr.user_service.repository.OfficerRepository;
import org.springframework.stereotype.Service;

@Service
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final UserService userService;

    public OfficerService(OfficerRepository officerRepository, UserService userService) {
        this.officerRepository = officerRepository;
        this.userService = userService;
    }

    public Officer createOfficerProfile(Long userId, OfficerCreateRequest req) {
        User user = userService.getUser(userId);
        userService.assertRole(user, Role.OFFICER);

        if (officerRepository.existsById(userId)) {
            throw new BadRequestException("Officer profile already exists for this user");
        }

        if (req.getEmployeeId() != null &&
                officerRepository.existsByEmployeeId(req.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists");
        }

        Officer o = new Officer();
        o.setUser(user);
        o.setEmployeeId(req.getEmployeeId());
        o.setDepartment(req.getDepartment());
        o.setDesignation(req.getDesignation());

        return officerRepository.save(o);
    }

    public Officer getOfficer(Long userId) {
        return officerRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Officer not found for userId " + userId));
    }

    public Officer updateOfficer(Long userId, OfficerCreateRequest req) {
        Officer o = getOfficer(userId);

        if (req.getEmployeeId() != null) o.setEmployeeId(req.getEmployeeId());
        if (req.getDepartment() != null) o.setDepartment(req.getDepartment());
        if (req.getDesignation() != null) o.setDesignation(req.getDesignation());

        return officerRepository.save(o);
    }

    // Admin can activate/deactivate officer
    public Officer setActive(Long userId, boolean active) {
        Officer o = getOfficer(userId);
        o.setIsActive(active);
        return officerRepository.save(o);
    }
}
