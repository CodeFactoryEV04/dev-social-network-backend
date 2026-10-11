package com.codefactory.dev_social_network.usuarios.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codefactory.dev_social_network.usuarios.entity.UserFollowEntity;

public interface UserFollowJpaRepository extends JpaRepository<UserFollowEntity, UUID> {

    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    List<UserFollowEntity> findByFollowerId(UUID followerId);
}