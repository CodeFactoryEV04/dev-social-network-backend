package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.CreateProjectRequest;
import com.codefactory.dev_social_network.proyectos.dto.ProjectResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.Technology;
import com.codefactory.dev_social_network.proyectos.repository.ProjectRepository;
import com.codefactory.dev_social_network.proyectos.repository.TechnologyRepository;
import com.codefactory.dev_social_network.shared.config.CurrentUserPort;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TechnologyRepository technologyRepository;
    private final CurrentUserPort currentUserPort;

    public ProjectService(
            ProjectRepository projectRepository,
            TechnologyRepository technologyRepository,
            CurrentUserPort currentUserPort
    ) {
        this.projectRepository = projectRepository;
        this.technologyRepository = technologyRepository;
        this.currentUserPort = currentUserPort;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {

        UUID userId = currentUserPort.currentUserId();

        validateRepositoryUrl(request.getRepositoryUrl());

        List<Technology> technologies =
                technologyRepository.findAllById(request.getTechnologyIds());

        if (technologies.size() != request.getTechnologyIds().size()) {
            throw new BusinessException(
                    "INVALID_TECHNOLOGY",
                    "One or more technologies are invalid",
                    HttpStatus.BAD_REQUEST
            );
        }

        Project project = new Project();

        project.setUserId(userId);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setTechnologies(technologies);

        OffsetDateTime now = OffsetDateTime.now();

        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        Project savedProject = projectRepository.save(project);

        return mapToResponse(savedProject);
    }

    private void validateRepositoryUrl(String repositoryUrl) {

        if (repositoryUrl == null || repositoryUrl.isBlank()) {
            return;
        }

        try {
            URI uri = URI.create(repositoryUrl);

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!"https".equalsIgnoreCase(scheme) || host == null) {
                throw new BusinessException(
                        "INVALID_REPOSITORY_URL",
                        "Repository URL must be a valid HTTPS URL",
                        HttpStatus.BAD_REQUEST
                );
            }

            String normalizedHost = host.toLowerCase();

            boolean allowedDomain =
                    normalizedHost.equals("github.com")
                            || normalizedHost.endsWith(".github.com")
                            || normalizedHost.equals("gitlab.com")
                            || normalizedHost.endsWith(".gitlab.com")
                            || normalizedHost.equals("bitbucket.org")
                            || normalizedHost.endsWith(".bitbucket.org");

            if (!allowedDomain) {
                throw new BusinessException(
                        "REPOSITORY_DOMAIN_NOT_ALLOWED",
                        "Repository URL must belong to GitHub, GitLab or Bitbucket",
                        HttpStatus.BAD_REQUEST
                );
            }

        } catch (BusinessException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new BusinessException(
                    "INVALID_REPOSITORY_URL",
                    "Invalid repository URL",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private ProjectResponse mapToResponse(Project project) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setUserId(project.getUserId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setRepositoryUrl(project.getRepositoryUrl());
        response.setCreatedAt(project.getCreatedAt());

        List<String> technologyNames = project.getTechnologies()
                .stream()
                .map(Technology::getName)
                .toList();

        response.setTechnologies(technologyNames);

        return response;
    }
}