package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.EmailDuplicadoException;
import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.EmailExistencePort;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;

@Component
@Order(4)
public class EmailUniqueRule implements RegistrationRule {

    private final EmailExistencePort emailExistencePort;

    public EmailUniqueRule(EmailExistencePort emailExistencePort) {
        this.emailExistencePort = emailExistencePort;
    }

    @Override
    public void validate(RegistrationData data) {
        if (emailExistencePort.existePorEmail(data.email())) {
            throw new EmailDuplicadoException(data.email());
        }
    }
}