package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class DiscusionYaResueltaException extends BusinessException {
    public DiscusionYaResueltaException() {
        super(
            "DISCUSION_YA_RESUELTA",
            "Está discusión ya ha sido resuelta",
            HttpStatus.CONFLICT
        );
    }
}