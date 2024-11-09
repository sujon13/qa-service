package com.example;

import com.example.qa.model.Auditable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserUtil {
    public String getUserName(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

    public String getUserName() {
        return getUserName(SecurityContextHolder.getContext().getAuthentication());
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public <T extends Auditable> boolean hasEditPermission(T entity) {
        return entity.getCreatedBy().equals(getUserName()) || isAdmin();
    }

    public <T extends Auditable> boolean hasFetchPermission(T entity) {
        return hasEditPermission(entity);
    }

}