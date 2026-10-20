package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class SelfFollowException extends BusinessException {
    public SelfFollowException() {
        super("AUTO_SEGUIMIENTO_NO_PERMITIDO", "No puedes seguirte a ti mismo", HttpStatus.BAD_REQUEST);
    }
}