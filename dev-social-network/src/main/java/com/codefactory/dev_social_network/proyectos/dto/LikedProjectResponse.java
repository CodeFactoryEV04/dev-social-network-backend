package com.codefactory.dev_social_network.proyectos.dto;

import java.time.OffsetDateTime;

public record LikedProjectResponse(
        Long projectId,
        String title,
        String repositoryUrl,
        OffsetDateTime likedAt
) {
}