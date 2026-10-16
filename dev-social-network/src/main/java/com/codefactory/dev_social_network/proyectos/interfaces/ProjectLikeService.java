package com.codefactory.dev_social_network.proyectos.interfaces;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectLikeService {

    ProjectLikeResponse likeProject(Long projectId, UUID usuarioId);

    List<LikedProjectResponse> getMyLikedProjects(UUID usuarioId);
}