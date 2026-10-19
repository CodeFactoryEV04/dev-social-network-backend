package com.codefactory.dev_social_network.comentarios.controller;
 
import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.service.ProjectCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/comments")
@Tag(
        name = "Project Comments",
        description = "Operations related to comments on projects"
)
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    public ProjectCommentController(ProjectCommentService projectCommentService) {
        this.projectCommentService = projectCommentService;
    }

    @Operation(
            summary = "Comment on a project",
            description = "Creates a new comment on a project, authored by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal UUID idUsuario,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = projectCommentService.createComment(idUsuario, projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "List comments on a project",
            description = "Returns all comments on a project, ordered from oldest to newest"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments returned successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping
    public ResponseEntity<List<CommentResponse>> listComments(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectCommentService.listComments(projectId));
    }
}