package com.codefactory.dev_social_network.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record FollowUserRequestDTO(@NotBlank String target) {
}