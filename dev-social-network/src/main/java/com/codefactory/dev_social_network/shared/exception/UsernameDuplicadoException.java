package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class UsernameDuplicadoException extends BusinessException {
    public UsernameDuplicadoException() {
        super("USERNAME_DUPLICADO", "Ese nombre de usuario ya está en uso", HttpStatus.CONFLICT);
    }
}