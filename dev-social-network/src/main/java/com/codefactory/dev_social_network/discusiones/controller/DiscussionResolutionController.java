package com.codefactory.dev_social_network.discusiones.controller;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.interfaces.ResolveDiscussionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/discussions")
@Tag(
        name = "Discussions",
        description = "Operations related to technical discussions"
)
public class DiscussionResolutionController {

    private final ResolveDiscussionUseCase resolveDiscussionUseCase;

    public DiscussionResolutionController(ResolveDiscussionUseCase resolveDiscussionUseCase) {
        this.resolveDiscussionUseCase = resolveDiscussionUseCase;
    }

    @Operation(
            summary = "Mark a discussion as resolved",
            description = "Marks a discussion as resolved. Only the author of the discussion can do it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Discussion marked as resolved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResolveDiscussionResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Only the author can resolve the discussion"),
            @ApiResponse(responseCode = "404", description = "Discussion not found"),
            @ApiResponse(responseCode = "409", description = "Discussion already resolved")
    })
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ResolveDiscussionResponse> resolveDiscussion(
            @AuthenticationPrincipal UUID userId,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(resolveDiscussionUseCase.resolve(userId, id));
    }
}