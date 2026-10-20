package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.shared.exception.UserNotFoundException;
import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;
import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowRule;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowTargetStrategy;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowUserUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowWriterPort;

@Service
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final List<FollowTargetStrategy> targetStrategies;
    private final FollowWriterPort followWriterPort;
    private final List<FollowRule> rules;

    public FollowUserUseCaseImpl(List<FollowTargetStrategy> targetStrategies,
                                 FollowWriterPort followWriterPort,
                                 List<FollowRule> rules) {
        this.targetStrategies = targetStrategies;
        this.followWriterPort = followWriterPort;
        this.rules = rules;
    }

    @Override
    @Transactional
    public FollowedUserDTO follow(UUID followerId, String target) {

        UserSummary destino = resolveTarget(target);

        for (FollowRule rule : rules) {
            rule.validate(followerId, destino.id());
        }

        followWriterPort.save(followerId, destino.id());

        return FollowedUserDTO.from(destino);
    }

    private UserSummary resolveTarget(String target) {
        return targetStrategies.stream()
                .filter(strategy -> strategy.supports(target))
                .findFirst()
                .flatMap(strategy -> strategy.resolve(target))
                .orElseThrow(UserNotFoundException::new);
    }
}