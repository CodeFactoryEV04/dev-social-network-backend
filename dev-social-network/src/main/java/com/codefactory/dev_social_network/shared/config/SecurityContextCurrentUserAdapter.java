package com.codefactory.dev_social_network.shared.config;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserAdapter implements CurrentUserPort {

    @Override
    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID id)) {
            throw new IllegalStateException("No hay un usuario autenticado con id UUID");
        }
        return id;
    }
}