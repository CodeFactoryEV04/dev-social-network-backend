package com.codefactory.dev_social_network.discusiones.dto;

import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;

import java.util.UUID;

public record DiscussionDetailResponse(Long id, String title, DiscussionStatus status, UUID userId) {
}