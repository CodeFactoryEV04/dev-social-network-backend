package com.codefactory.dev_social_network.mensajeria.repository;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationReadStateEntity;
import com.codefactory.dev_social_network.mensajeria.interfaces.ConversationReadPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class ConversationReadAdapter implements ConversationReadPort {

    private final ConversationRepository conversationRepository;
    private final ConversationReadStateRepository readStateRepository;

    public ConversationReadAdapter(ConversationRepository conversationRepository,
                                   ConversationReadStateRepository readStateRepository) {
        this.conversationRepository = conversationRepository;
        this.readStateRepository = readStateRepository;
    }

    @Override
    @Transactional
    public void markAsRead(UUID readerId, UUID conversationId, Instant readAt) {
        conversationRepository.findById(conversationId).ifPresent(conversation -> {
            ConversationReadStateEntity state = readStateRepository
                    .findByConversation_IdAndUserId(conversationId, readerId)
                    .orElseGet(() -> new ConversationReadStateEntity(conversation, readerId, readAt));
            state.advanceTo(readAt);
            readStateRepository.save(state);
        });
    }
}