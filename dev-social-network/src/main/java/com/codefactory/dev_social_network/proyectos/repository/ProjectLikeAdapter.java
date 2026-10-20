package com.codefactory.dev_social_network.proyectos.repository;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.ProjectLike;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikePort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProjectLikeAdapter implements ProjectLikePort {

    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectRepository projectRepository;

    public ProjectLikeAdapter(ProjectLikeRepository projectLikeRepository,
                              ProjectRepository projectRepository) {
        this.projectLikeRepository = projectLikeRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean registrarLike(Long projectId, UUID usuarioId) {
        if (projectLikeRepository.existsByProject_IdAndUsuarioId(projectId, usuarioId)) {
            return false;
        }
        try {
            Project project = projectRepository.getReferenceById(projectId);
            projectLikeRepository.saveAndFlush(new ProjectLike(project, usuarioId));
            return true;
        } catch (DataIntegrityViolationException e) {
            // Dos peticiones simultáneas: la restricción única evita el duplicado
            return false;
        }
    }

    @Override
    public long contarLikes(Long projectId) {
        return projectLikeRepository.countByProject_Id(projectId);
    }

    @Override
    public List<LikedProjectResponse> listarPorUsuario(UUID usuarioId) {
        return projectLikeRepository.findAllByUsuarioIdWithProject(usuarioId).stream()
                .map(like -> new LikedProjectResponse(
                        like.getProject().getId(),
                        like.getProject().getTitle(),
                        like.getProject().getRepositoryUrl(),
                        like.getCreatedAt()))
                .toList();
    }
}
