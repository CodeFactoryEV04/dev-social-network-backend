package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;

@Component
@Order(1)
public class UsernameFormatRule implements RegistrationRule {

    private final UsernameValidator usernameValidator;

    public UsernameFormatRule(UsernameValidator usernameValidator) {
        this.usernameValidator = usernameValidator;
    }

    @Override
    public void validate(RegistrationData data) {
        usernameValidator.validar(data.username());
    }
}