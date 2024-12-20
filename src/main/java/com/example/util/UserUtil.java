package com.example.util;

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

    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                //.anyMatch(auth -> auth.getAuthority().equals("ROLE" + role));
                .anyMatch(auth ->
                        java.util.Arrays.stream(roles)
                                .anyMatch(role -> auth.getAuthority().equals("ROLE_" + role))
                );
    }

    public boolean isAdmin() {
        return hasAnyRole("ADMIN");
    }

    public boolean isUser() {
        return hasAnyRole("USER");
    }

    public <T extends Auditable> boolean isCreator(T entity) {
        return entity.getCreatedBy().equals(getUserName());
    }

    public <T extends Auditable> boolean hasEditPermission(T entity) {
        return isCreator(entity) || isAdmin();
    }

    public <T extends Auditable> boolean hasFetchPermission(T entity) {
        return hasEditPermission(entity);
    }

}
