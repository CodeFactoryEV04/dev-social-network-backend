package com.codefactory.dev_social_network.mensajeria.repository;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends Repository<ConversationEntity, UUID> {

    Optional<ConversationEntity> findByParticipantOneIdAndParticipantTwoId(UUID participantOneId, UUID participantTwoId);

    Optional<ConversationEntity> findById(UUID id);

    ConversationEntity save(ConversationEntity conversation);
}