package com.codefactory.dev_social_network.usuarios.service;

import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowTargetStrategy;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByUsernamePort;

@Component
@Order(2)
public class UsernameTargetStrategy implements FollowTargetStrategy {

    private final UserFinderByUsernamePort userFinderByUsernamePort;

    public UsernameTargetStrategy(UserFinderByUsernamePort userFinderByUsernamePort) {
        this.userFinderByUsernamePort = userFinderByUsernamePort;
    }

    @Override
    public boolean supports(String target) {
        return !target.contains("@");
    }

    @Override
    public Optional<UserSummary> resolve(String target) {
        return userFinderByUsernamePort.buscarPorUsername(target);
    }
}