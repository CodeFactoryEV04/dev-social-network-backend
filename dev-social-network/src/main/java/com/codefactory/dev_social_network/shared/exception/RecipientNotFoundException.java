package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class RecipientNotFoundException extends BusinessException {
    public RecipientNotFoundException() {
        super("RECIPIENT_NOT_FOUND", "Usuario no existe", HttpStatus.NOT_FOUND);
    }
}