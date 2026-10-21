package com.codefactory.dev_social_network.discusiones.dto;

import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;

import java.util.UUID;

public record ResolveDiscussionResponse(UUID id, DiscussionStatus status, String message) {
}

