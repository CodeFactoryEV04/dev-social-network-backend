package com.codefactory.dev_social_network.usuarios.service;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowTargetStrategy;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByUsernamePort;

@Component
public class UsernameTargetStrategy implements FollowTargetStrategy {

    private final UserFinderByUsernamePort userFinderByUsernamePort;
    private final UsernameValidator usernameValidator;

    public UsernameTargetStrategy(UserFinderByUsernamePort userFinderByUsernamePort,
                                  UsernameValidator usernameValidator) {
        this.userFinderByUsernamePort = userFinderByUsernamePort;
        this.usernameValidator = usernameValidator;
    }

    // Solo acepta texto con formato de username. Ese formato no permite "@", así que no se solapa con el correo.
    @Override
    public boolean supports(String target) {
        return usernameValidator.esValido(target);
    }

    @Override
    public Optional<UserSummary> resolve(String target) {
        return userFinderByUsernamePort.buscarPorUsername(target);
    }
}