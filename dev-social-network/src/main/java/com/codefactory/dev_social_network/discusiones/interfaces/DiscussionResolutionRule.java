package com.codefactory.dev_social_network.discusiones.interfaces;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;

import java.util.UUID;

public interface DiscussionResolutionRule {

    void validate(Discussion discussion, UUID autorId);
}

