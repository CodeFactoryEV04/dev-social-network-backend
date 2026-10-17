package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.Optional;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;

public interface UserFinderByEmailPort {

    Optional<UserSummary> buscarPorEmail(String email);
}