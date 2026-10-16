package com.codefactory.dev_social_network.discusiones.controller;

import com.codefactory.dev_social_network.discusiones.dto.DiscussionDetailResponse;
import com.codefactory.dev_social_network.discusiones.interfaces.GetDiscussionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/discussions")
@Tag(
        name = "Discussions",
        description = "Operations related to technical discussions"
)
public class DiscussionQueryController {

    private final GetDiscussionUseCase getDiscussionUseCase;

    public DiscussionQueryController(GetDiscussionUseCase getDiscussionUseCase) {
        this.getDiscussionUseCase = getDiscussionUseCase;
    }

    @Operation(
            summary = "Get a discussion by id",
            description = "Returns the id, title, status (OPEN or RESOLVED) and author of a discussion. Any authenticated user can query it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Discussion found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DiscussionDetailResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Missing or invalid authentication token"),
            @ApiResponse(responseCode = "404", description = "Discussion not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDetailResponse> getDiscussion(@PathVariable("id") Long id) {
        return ResponseEntity.ok(getDiscussionUseCase.getById(id));
    }
}