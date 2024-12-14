package org.atlas.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public boolean canEdit(Long userId) {
        if (userId != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                return userId.equals(authentication.getPrincipal());
            }
        }
        return false;
    }
}
