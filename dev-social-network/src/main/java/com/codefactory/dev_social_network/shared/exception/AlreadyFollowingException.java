package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFollowingException extends BusinessException {
    public AlreadyFollowingException() {
        super("YA_SIGUES_AL_USUARIO", "Ya estás siguiendo este usuario", HttpStatus.CONFLICT);
    }
}