package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("USUARIO_NO_ENCONTRADO", "Usuario no existe", HttpStatus.NOT_FOUND);
    }
}