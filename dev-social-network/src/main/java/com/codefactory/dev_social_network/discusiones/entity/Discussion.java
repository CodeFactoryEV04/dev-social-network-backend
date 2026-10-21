package com.codefactory.dev_social_network.discusiones.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "discussions")
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "technology_id", nullable = false)
    private Long technologyId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscussionStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Discussion() {
    }

    public Discussion(UUID userId, Long technologyId, String title, String content) {
        OffsetDateTime now = OffsetDateTime.now();
        this.userId = userId;
        this.technologyId = technologyId;
        this.title = title;
        this.content = content;
        this.status = DiscussionStatus.OPEN;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Long getTechnologyId() {
        return technologyId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public DiscussionStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(DiscussionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String contentPreview = content != null && content.length() > 50
                ? content.substring(0, 50) + "..."
                : content;
        return "Discussion{" +
                "id=" + id +
                ", userId=" + userId +
                ", technologyId=" + technologyId +
                ", title='" + title + '\'' +
                ", content='" + contentPreview + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}