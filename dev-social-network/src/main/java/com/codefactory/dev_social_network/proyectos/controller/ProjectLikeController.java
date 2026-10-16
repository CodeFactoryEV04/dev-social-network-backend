package com.codefactory.dev_social_network.proyectos.controller;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Likes", description = "Likes on developer projects")
public class ProjectLikeController {

    private final ProjectLikeService projectLikeService;

    public ProjectLikeController(ProjectLikeService projectLikeService) {
        this.projectLikeService = projectLikeService;
    }

    @Operation(summary = "Like a project",
            description = "Registers a like from the authenticated user on the given project")
    @PostMapping("/{projectId}/likes")
    public ResponseEntity<ProjectLikeResponse> likeProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UUID usuarioId
    ) {
        ProjectLikeResponse response = projectLikeService.likeProject(projectId, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List my liked projects",
            description = "Returns the projects liked by the authenticated user, most recent first")
    @GetMapping("/likes/me")
    public ResponseEntity<List<LikedProjectResponse>> getMyLikedProjects(
            @AuthenticationPrincipal UUID usuarioId
    ) {
        return ResponseEntity.ok(projectLikeService.getMyLikedProjects(usuarioId));
    }
}