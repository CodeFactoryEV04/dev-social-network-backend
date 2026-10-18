package com.codefactory.dev_social_network.Mensajeria.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversaciones")
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "participante_uno", nullable = false)
    private UUID participantOne;

    @Column(name = "participante_dos", nullable = false)
    private UUID participantTwo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    public ConversationEntity() {}

    public ConversationEntity(UUID participantOne, UUID participantTwo, LocalDateTime createdAt) {
        this.participantOne = participantOne;
        this.participantTwo = participantTwo;
        this.createdAt = LocalDateTime.now();;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParticipantOne() {
        return participantOne;
    }

    public void setParticipantOne(UUID participantOne) {
        this.participantOne = participantOne;
    }

    public UUID getParticipantTwo() {
        return participantTwo;
    }

    public void setParticipantTwo(UUID participantTwo) {
        this.participantTwo = participantTwo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

}
