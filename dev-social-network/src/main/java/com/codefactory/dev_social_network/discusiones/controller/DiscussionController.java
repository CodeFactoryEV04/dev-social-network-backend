package com.codefactory.dev_social_network.discusiones.controller;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.service.DiscussionService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/discussions")
@Tag(
        name = "Discussions",
        description = "Operations related to technical discussions"
)
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @Operation(
            summary = "Create a discussion",
            description = "Creates a new discussion for the authenticated user, classified under one technology"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Discussion created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DiscussionResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<DiscussionResponse> createDiscussion(
            @AuthenticationPrincipal UUID autorId,
            @Valid @RequestBody CreateDiscussionRequest request
    ) {
        DiscussionResponse response = discussionService.createDiscussion(autorId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
