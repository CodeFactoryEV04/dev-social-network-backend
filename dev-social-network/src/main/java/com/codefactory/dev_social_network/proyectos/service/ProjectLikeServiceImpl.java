package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.ProjectLike;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikeService;
import com.codefactory.dev_social_network.proyectos.repository.ProjectLikeRepository;
import com.codefactory.dev_social_network.proyectos.repository.ProjectRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectLikeServiceImpl implements ProjectLikeService {

    private static final String MSG_ALREADY_LIKED = "Ya le haz dado like a este proyecto";
    private static final String MSG_NOT_FOUND = "El proyecto no existe";

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;

    public ProjectLikeServiceImpl(ProjectRepository projectRepository,
                                  ProjectLikeRepository projectLikeRepository) {
        this.projectRepository = projectRepository;
        this.projectLikeRepository = projectLikeRepository;
    }

    @Override
    @Transactional
    public ProjectLikeResponse likeProject(Long projectId, UUID usuarioId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(
                        "PROJECT_NOT_FOUND", MSG_NOT_FOUND, HttpStatus.NOT_FOUND));


        if (projectLikeRepository.existsByProject_IdAndUsuarioId(projectId, usuarioId)) {
            throw alreadyLiked();
        }

      
        try {
            projectLikeRepository.saveAndFlush(new ProjectLike(project, usuarioId));
        } catch (DataIntegrityViolationException e) {
        
            throw alreadyLiked();
        }

        long total = projectLikeRepository.countByProject_Id(projectId);
        return new ProjectLikeResponse(project.getId(), project.getTitle(), total, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikedProjectResponse> getMyLikedProjects(UUID usuarioId) {
        return projectLikeRepository.findAllByUsuarioIdWithProject(usuarioId).stream()
                .map(like -> new LikedProjectResponse(
                        like.getProject().getId(),
                        like.getProject().getTitle(),
                        like.getProject().getRepositoryUrl(),
                        like.getCreatedAt()))
                .toList();
    }

    private BusinessException alreadyLiked() {
        return new BusinessException("PROJECT_ALREADY_LIKED", MSG_ALREADY_LIKED, HttpStatus.CONFLICT);
    }
}