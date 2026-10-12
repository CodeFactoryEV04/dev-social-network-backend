package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;

@Component
@Order(2)
public class EmailFormatRule implements RegistrationRule {

    private final EmailValidator emailValidator;

    public EmailFormatRule(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    @Override
    public void validate(RegistrationData data) {
        emailValidator.validar(data.email());
    }
}