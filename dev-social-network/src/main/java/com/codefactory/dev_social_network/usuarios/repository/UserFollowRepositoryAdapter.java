package com.codefactory.dev_social_network.usuarios.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.entity.UserFollowEntity;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFollowRepositoryPort;

@Component
public class UserFollowRepositoryAdapter implements UserFollowRepositoryPort {

    private final UserFollowJpaRepository jpaRepository;

    public UserFollowRepositoryAdapter(UserFollowJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean exists(UUID followerId, UUID followedId) {
        return jpaRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    @Override
    public void save(UUID followerId, UUID followedId) {
        jpaRepository.save(new UserFollowEntity(followerId, followedId));
    }

    @Override
    public List<UUID> findFollowedIds(UUID followerId) {
        return jpaRepository.findByFollowerId(followerId).stream()
                .map(UserFollowEntity::getFollowedId)
                .toList();
    }
}