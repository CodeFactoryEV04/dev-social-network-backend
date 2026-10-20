package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class FormatoUsernameInvalidoException extends BusinessException {
    public FormatoUsernameInvalidoException() {
        super("FORMATO_USERNAME_INVALIDO",
              "El nombre de usuario debe tener de 3 a 50 caracteres: letras, números, punto, guion o guion bajo",
              HttpStatus.BAD_REQUEST);
    }
}