package com.codefactory.dev_social_network.discusiones.interfaces;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;

import java.util.Optional;
import java.util.UUID;

public interface DiscussionFinderPort {

    Optional<Discussion> findById(UUID discussionId);
}