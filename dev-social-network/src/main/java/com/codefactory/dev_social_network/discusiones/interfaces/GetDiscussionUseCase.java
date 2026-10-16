package com.codefactory.dev_social_network.discusiones.interfaces;

import com.codefactory.dev_social_network.discusiones.dto.DiscussionDetailResponse;

public interface GetDiscussionUseCase {

    DiscussionDetailResponse getById(Long discussionId);
}