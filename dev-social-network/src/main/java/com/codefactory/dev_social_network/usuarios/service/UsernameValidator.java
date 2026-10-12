package com.codefactory.dev_social_network.usuarios.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.FormatoUsernameInvalidoException;

@Component
public class UsernameValidator {

    private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");

    public void validar(String username) {
        if (username == null || !USERNAME_REGEX.matcher(username).matches()) {
            throw new FormatoUsernameInvalidoException();
        }
    }
}