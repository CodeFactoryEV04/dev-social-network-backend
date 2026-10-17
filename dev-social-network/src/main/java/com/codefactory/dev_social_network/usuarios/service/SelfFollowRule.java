package com.codefactory.dev_social_network.usuarios.service;

import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.SelfFollowException;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowRule;

@Component
@Order(1)
public class SelfFollowRule implements FollowRule {

    @Override
    public void validate(UUID followerId, UUID targetId) {
        if (targetId.equals(followerId)) {
            throw new SelfFollowException();
        }
    }
}