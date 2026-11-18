package com.codefactory.dev_social_network.mensajeria.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation_read_states",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_read_state_conversation_user",
                columnNames = {"conversation_id", "user_id"}))
public class ConversationReadStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    protected ConversationReadStateEntity() {
    }

    public ConversationReadStateEntity(ConversationEntity conversation, UUID userId, Instant lastReadAt) {
        this.conversation = conversation;
        this.userId = userId;
        this.lastReadAt = lastReadAt;
    }

    public void advanceTo(Instant readAt) {
        if (readAt.isAfter(lastReadAt)) {
            lastReadAt = readAt;
        }
    }

    public UUID getId() { return id; }
    public ConversationEntity getConversation() { return conversation; }
    public UUID getUserId() { return userId; }
    public Instant getLastReadAt() { return lastReadAt; }
}