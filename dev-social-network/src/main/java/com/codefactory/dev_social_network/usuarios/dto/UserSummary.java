package com.codefactory.dev_social_network.usuarios.dto;

import java.util.UUID;

public record UserSummary(UUID id, String username, String nombre, String apellido, String email) {
}