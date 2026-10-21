package com.codefactory.dev_social_network.usuarios.service;

import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.AlreadyFollowingException;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowExistencePort;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowRule;

@Component
@Order(2)
public class AlreadyFollowingRule implements FollowRule {

    private final FollowExistencePort followExistencePort;

    public AlreadyFollowingRule(FollowExistencePort followExistencePort) {
        this.followExistencePort = followExistencePort;
    }

    @Override
    public void validate(UUID followerId, UUID targetId) {
        if (followExistencePort.exists(followerId, targetId)) {
            throw new AlreadyFollowingException();
        }
    }
}