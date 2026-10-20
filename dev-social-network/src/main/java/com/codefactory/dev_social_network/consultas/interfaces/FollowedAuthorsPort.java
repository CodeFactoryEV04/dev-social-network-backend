package com.codefactory.dev_social_network.consultas.interfaces;

import java.util.List;
import java.util.UUID;

public interface FollowedAuthorsPort {

    List<UUID> findFollowedIds(UUID userId);
}