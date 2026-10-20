package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.List;
import java.util.UUID;

import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;

public interface ListFollowingUseCase {

    List<FollowedUserDTO> list(UUID followerId);
}