package com.codefactory.dev_social_network.consultas.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.consultas.interfaces.FollowedAuthorsPort;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowReaderPort;

@Component
public class FollowedAuthorsAdapter implements FollowedAuthorsPort {

    private final FollowReaderPort followReaderPort;

    public FollowedAuthorsAdapter(FollowReaderPort followReaderPort) {
        this.followReaderPort = followReaderPort;
    }

    @Override
    public List<UUID> findFollowedIds(UUID userId) {
        return followReaderPort.findFollowedIds(userId);
    }
}