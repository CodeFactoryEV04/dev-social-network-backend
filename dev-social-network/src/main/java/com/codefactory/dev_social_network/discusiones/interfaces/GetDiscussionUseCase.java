package com.codefactory.dev_social_network.discusiones.interfaces;

import com.codefactory.dev_social_network.discusiones.dto.DiscussionDetailResponse;

import java.util.UUID;

public interface GetDiscussionUseCase {

    DiscussionDetailResponse getById(UUID discussionId);
}

