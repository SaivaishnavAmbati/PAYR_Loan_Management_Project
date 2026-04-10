package com.payr.loan_service.config;

import com.payr.loan_service.dto.AuthPrinciple;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        AuthPrinciple principal = (AuthPrinciple) authentication.getPrincipal();

        if(principal.getId()==null) throw new RuntimeException("Unauthorized request");
        return principal.getId();
    }
    public static String getCurrentEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        AuthPrinciple principal = (AuthPrinciple) authentication.getPrincipal();

        if(principal.getUsername()==null) throw new RuntimeException("Unauthorized request");
        return principal.getUsername();
    }
}
