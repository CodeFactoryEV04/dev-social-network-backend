package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationRequiredException extends BusinessException {
    public AuthenticationRequiredException() {
        super("AUTHENTICATION_REQUIRED", "Debes iniciar sesión para continuar", HttpStatus.UNAUTHORIZED);
    }
}