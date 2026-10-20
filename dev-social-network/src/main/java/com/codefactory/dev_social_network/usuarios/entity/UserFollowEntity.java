package com.codefactory.dev_social_network.usuarios.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
public class UserFollowEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "followed_id", nullable = false)
    private UUID followedId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserFollowEntity() {
    }

    public UserFollowEntity(UUID followerId, UUID followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getFollowedId() {
        return followedId;
    }
}