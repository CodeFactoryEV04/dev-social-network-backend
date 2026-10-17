package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.UsernameDuplicadoException;
import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;
import com.codefactory.dev_social_network.usuarios.interfaces.UsernameExistencePort;

@Component
@Order(5)
public class UsernameUniqueRule implements RegistrationRule {

    private final UsernameExistencePort usernameExistencePort;

    public UsernameUniqueRule(UsernameExistencePort usernameExistencePort) {
        this.usernameExistencePort = usernameExistencePort;
    }

    @Override
    public void validate(RegistrationData data) {
        if (usernameExistencePort.existePorUsername(data.username())) {
            throw new UsernameDuplicadoException();
        }
    }
}