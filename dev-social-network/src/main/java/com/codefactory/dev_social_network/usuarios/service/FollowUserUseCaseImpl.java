package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;
import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowRule;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowUserUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowWriterPort;

@Service
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final FollowTargetResolver followTargetResolver;
    private final FollowWriterPort followWriterPort;
    private final List<FollowRule> rules;

    public FollowUserUseCaseImpl(FollowTargetResolver followTargetResolver,
                                 FollowWriterPort followWriterPort,
                                 List<FollowRule> rules) {
        this.followTargetResolver = followTargetResolver;
        this.followWriterPort = followWriterPort;
        this.rules = rules;
    }

    @Override
    @Transactional
    public FollowedUserDTO follow(UUID followerId, String target) {

        UserSummary destino = followTargetResolver.resolve(target);

        for (FollowRule rule : rules) {
            rule.validate(followerId, destino.id());
        }

        followWriterPort.save(followerId, destino.id());

        return FollowedUserDTO.from(destino);
    }
}