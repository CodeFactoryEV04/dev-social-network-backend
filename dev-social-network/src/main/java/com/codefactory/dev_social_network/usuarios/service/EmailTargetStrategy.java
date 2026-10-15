package com.codefactory.dev_social_network.usuarios.service;

import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowTargetStrategy;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByEmailPort;

@Component
@Order(1)
public class EmailTargetStrategy implements FollowTargetStrategy {

    private final UserFinderByEmailPort userFinderByEmailPort;

    public EmailTargetStrategy(UserFinderByEmailPort userFinderByEmailPort) {
        this.userFinderByEmailPort = userFinderByEmailPort;
    }

    @Override
    public boolean supports(String target) {
        return target.contains("@");
    }

    @Override
    public Optional<UserSummary> resolve(String target) {
        return userFinderByEmailPort.buscarPorEmail(target);
    }
}