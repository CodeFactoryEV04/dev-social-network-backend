package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowReaderPort;
import com.codefactory.dev_social_network.usuarios.interfaces.ListFollowingUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByIdPort;

@Service
public class ListFollowingUseCaseImpl implements ListFollowingUseCase {

    private final FollowReaderPort followReaderPort;
    private final UserFinderByIdPort userFinderByIdPort;

    public ListFollowingUseCaseImpl(FollowReaderPort followReaderPort,
                                    UserFinderByIdPort userFinderByIdPort) {
        this.followReaderPort = followReaderPort;
        this.userFinderByIdPort = userFinderByIdPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowedUserDTO> list(UUID followerId) {
        return followReaderPort.findFollowedIds(followerId).stream()
                .map(userFinderByIdPort::buscarPorId)
                .flatMap(Optional::stream)
                .map(FollowedUserDTO::from)
                .toList();
    }
}