package com.codefactory.dev_social_network.discusiones.interfaces;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;

import java.util.UUID;

public interface ResolveDiscussionUseCase {

    ResolveDiscussionResponse resolve(UUID autorId, UUID discussionId);
}

