package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectSummary;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectFinderPort;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikePort;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikeService;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectLikeServiceImpl implements ProjectLikeService {

    private static final String MSG_ALREADY_LIKED = "Ya le haz dado like a este proyecto";
    private static final String MSG_NOT_FOUND = "El proyecto no existe";

    private final ProjectFinderPort projectFinderPort;
    private final ProjectLikePort projectLikePort;

    public ProjectLikeServiceImpl(ProjectFinderPort projectFinderPort,
                                  ProjectLikePort projectLikePort) {
        this.projectFinderPort = projectFinderPort;
        this.projectLikePort = projectLikePort;
    }

    @Override
    @Transactional
    public ProjectLikeResponse likeProject(Long projectId, UUID usuarioId) {

        ProjectSummary project = projectFinderPort.buscarResumenPorId(projectId)
                .orElseThrow(() -> new BusinessException(
                        "PROJECT_NOT_FOUND", MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!projectLikePort.registrarLike(projectId, usuarioId)) {
            throw new BusinessException(
                    "PROJECT_ALREADY_LIKED", MSG_ALREADY_LIKED, HttpStatus.CONFLICT);
        }

        long total = projectLikePort.contarLikes(projectId);
        return new ProjectLikeResponse(project.id(), project.title(), total, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikedProjectResponse> getMyLikedProjects(UUID usuarioId) {
        return projectLikePort.listarPorUsuario(usuarioId);
    }
}