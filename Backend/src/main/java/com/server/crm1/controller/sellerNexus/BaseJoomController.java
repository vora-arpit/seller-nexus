package com.server.crm1.controller.sellerNexus;

import com.server.crm1.model.users.User;
import com.server.crm1.repository.user.UserRepository;
import com.server.crm1.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base controller providing common functionality for all JOOM controllers.
 * Contains shared utilities like user authentication and authorization checks.
 */
@RequiredArgsConstructor
public abstract class BaseJoomController {

    protected final UserRepository userRepository;

    /**
     * Resolve current logged-in User from the SecurityContext safely.
     * Returns null when no authenticated user or when the principal is not our UserPrincipal.
     */
    protected User getCurrentUserFromSecurity() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();
            if (principal instanceof UserPrincipal) {
                Integer uid = ((UserPrincipal) principal).getId();
                return userRepository.findById(uid).orElse(null);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
