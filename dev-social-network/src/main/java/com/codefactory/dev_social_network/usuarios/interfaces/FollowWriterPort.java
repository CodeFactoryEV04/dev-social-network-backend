package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

public interface FollowWriterPort {

    void save(UUID followerId, UUID followedId);
}