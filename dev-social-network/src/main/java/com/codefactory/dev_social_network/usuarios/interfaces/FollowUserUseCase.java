package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;

public interface FollowUserUseCase {

    FollowedUserDTO follow(UUID followerId, String targetEmail);
}