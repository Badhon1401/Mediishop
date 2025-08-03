package com.sda_project.medishop.infrastructure.utils;

import com.sda_project.medishop.domain.User;
import com.sda_project.medishop.domain.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class UserCredentials {

    // Method to get the current user's username
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUsername();
        }
        return null;
    }

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        return null;
    }



    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User is not authenticated");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }
}
