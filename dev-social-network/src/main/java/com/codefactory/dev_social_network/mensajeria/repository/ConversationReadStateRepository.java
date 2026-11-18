package com.codefactory.dev_social_network.mensajeria.repository;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationReadStateEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationReadStateRepository extends Repository<ConversationReadStateEntity, UUID> {

    Optional<ConversationReadStateEntity> findByConversation_IdAndUserId(UUID conversationId, UUID userId);

    ConversationReadStateEntity save(ConversationReadStateEntity state);
}