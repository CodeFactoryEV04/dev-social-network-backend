package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.interfaces.AuthenticatedUserProvider;
import com.codefactory.dev_social_network.shared.exception.AuthenticationRequiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextAuthenticatedUserProvider implements AuthenticatedUserProvider {

    @Override
    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        throw new AuthenticationRequiredException();
    }
}