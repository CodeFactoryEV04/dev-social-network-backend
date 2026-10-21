package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class SoloAutorPuedeResolverException extends BusinessException {
    public SoloAutorPuedeResolverException() {
        super(
            "SOLO_AUTOR_PUEDE_RESOLVER",
            "Solo el autor puede marcar como resuelta la discusión",
            HttpStatus.FORBIDDEN
        );
    }
}