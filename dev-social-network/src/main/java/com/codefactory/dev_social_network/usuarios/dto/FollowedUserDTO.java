package com.codefactory.dev_social_network.usuarios.dto;

import java.util.UUID;

public record FollowedUserDTO(UUID id, String username, String fullName, String email) {

    public static FollowedUserDTO from(UserSummary user) {
        return new FollowedUserDTO(
                user.id(),
                user.username(),
                user.nombre() + " " + user.apellido(),
                user.email());
    }
}