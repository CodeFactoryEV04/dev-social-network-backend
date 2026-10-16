package com.codefactory.dev_social_network.proyectos.dto;

public record ProjectLikeResponse(
        Long projectId,
        String projectTitle,
        long likesCount,
        boolean liked
) {
}