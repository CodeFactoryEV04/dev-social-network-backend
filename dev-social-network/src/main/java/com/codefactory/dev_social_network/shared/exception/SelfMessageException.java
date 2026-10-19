package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class SelfMessageException extends BusinessException {
    public SelfMessageException() {
        super("SELF_MESSAGE_NOT_ALLOWED", "No puedes enviarte un mensaje a ti mismo", HttpStatus.BAD_REQUEST);
    }
}