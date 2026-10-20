package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.List;
import java.util.UUID;

public interface FollowReaderPort {

    List<UUID> findFollowedIds(UUID followerId);
}