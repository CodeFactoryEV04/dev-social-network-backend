package com.codefactory.dev_social_network.discusiones.dto;

import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;

public record ResolveDiscussionResponse(Long id, DiscussionStatus status, String message) {
}