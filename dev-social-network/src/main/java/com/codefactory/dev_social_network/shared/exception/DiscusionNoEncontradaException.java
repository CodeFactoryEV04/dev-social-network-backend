package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class DiscusionNoEncontradaException extends BusinessException {
    public DiscusionNoEncontradaException() {
        super(
            "DISCUSION_NO_ENCONTRADA",
            "Está discusión no existe",
            HttpStatus.NOT_FOUND
        );
    }
}