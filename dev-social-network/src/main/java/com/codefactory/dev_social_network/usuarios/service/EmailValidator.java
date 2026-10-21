package com.codefactory.dev_social_network.usuarios.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.FormatoEmailInvalidoException;

@Component
public class EmailValidator {

    private static final Pattern EMAIL_REGEX =
        Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public void validar(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new FormatoEmailInvalidoException(email);
        }
    }
}