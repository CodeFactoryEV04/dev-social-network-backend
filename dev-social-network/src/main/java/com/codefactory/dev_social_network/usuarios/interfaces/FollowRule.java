package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

public interface FollowRule {

    void validate(UUID followerId, UUID targetId);
}