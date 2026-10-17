package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;

@Component
@Order(3)
public class PasswordStrengthRule implements RegistrationRule {

    private final PasswordValidator passwordValidator;

    public PasswordStrengthRule(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public void validate(RegistrationData data) {
        passwordValidator.validar(data.contrasena());
    }
}