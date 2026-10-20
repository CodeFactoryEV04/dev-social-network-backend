package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;

public interface UserFinderByIdPort {

    Optional<UserSummary> buscarPorId(UUID id);
}