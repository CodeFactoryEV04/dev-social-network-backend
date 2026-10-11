package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.shared.exception.UserNotFoundException;
import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;
import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowRule;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowUserUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowWriterPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByEmailPort;

@Service
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final UserFinderByEmailPort userFinderByEmailPort;
    private final FollowWriterPort followWriterPort;
    private final List<FollowRule> rules;

    public FollowUserUseCaseImpl(UserFinderByEmailPort userFinderByEmailPort,
                                 FollowWriterPort followWriterPort,
                                 List<FollowRule> rules) {
        this.userFinderByEmailPort = userFinderByEmailPort;
        this.followWriterPort = followWriterPort;
        this.rules = rules;
    }

    @Override
    @Transactional
    public FollowedUserDTO follow(UUID followerId, String targetEmail) {

        UserSummary target = userFinderByEmailPort.buscarPorEmail(targetEmail)
                .orElseThrow(UserNotFoundException::new);

        for (FollowRule rule : rules) {
            rule.validate(followerId, target.id());
        }

        followWriterPort.save(followerId, target.id());

        return FollowedUserDTO.from(target);
    }
}