package com.codefactory.dev_social_network.Mensajeria.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_conversation_participants",
                columnNames = {"participant_one_id", "participant_two_id"}))
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "participant_one_id", nullable = false)
    private UUID participantOneId;

    @Column(name = "participant_two_id", nullable = false)
    private UUID participantTwoId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ConversationEntity() {
    }

    public UUID getId() { return id; }
    public UUID getParticipantOneId() { return participantOneId; }
    public void setParticipantOneId(UUID participantOneId) { this.participantOneId = participantOneId; }
    public UUID getParticipantTwoId() { return participantTwoId; }
    public void setParticipantTwoId(UUID participantTwoId) { this.participantTwoId = participantTwoId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}