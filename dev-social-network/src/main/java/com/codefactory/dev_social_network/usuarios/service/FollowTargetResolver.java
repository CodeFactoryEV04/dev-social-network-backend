package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.UserNotFoundException;
import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByEmailPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByUsernamePort;

@Component
public class FollowTargetResolver {

    private final UserFinderByEmailPort userFinderByEmailPort;
    private final UserFinderByUsernamePort userFinderByUsernamePort;

    public FollowTargetResolver(UserFinderByEmailPort userFinderByEmailPort,
                                UserFinderByUsernamePort userFinderByUsernamePort) {
        this.userFinderByEmailPort = userFinderByEmailPort;
        this.userFinderByUsernamePort = userFinderByUsernamePort;
    }

    // UsernameValidator no permite "@", así que la presencia de "@" distingue correo de username
    public UserSummary resolve(String target) {
        return (target.contains("@")
                ? userFinderByEmailPort.buscarPorEmail(target)
                : userFinderByUsernamePort.buscarPorUsername(target))
                .orElseThrow(UserNotFoundException::new);
    }
}