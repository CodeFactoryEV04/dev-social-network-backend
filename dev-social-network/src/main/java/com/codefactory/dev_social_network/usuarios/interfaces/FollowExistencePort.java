package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

public interface FollowExistencePort {

    boolean exists(UUID followerId, UUID followedId);
}