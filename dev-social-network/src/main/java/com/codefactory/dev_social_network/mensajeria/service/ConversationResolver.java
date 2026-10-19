package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import com.codefactory.dev_social_network.mensajeria.repository.ConversationRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Única responsabilidad: dado un par de usuarios, devolver SU conversación
 * (la existente o una nueva), aplicando el orden canónico del par.
 */
@Component
public class ConversationResolver {

    private final ConversationRepository conversationRepository;
    private final Clock clock;

    public ConversationResolver(ConversationRepository conversationRepository, Clock clock) {
        this.conversationRepository = conversationRepository;
        this.clock = clock;
    }

    public ConversationEntity resolve(UUID userA, UUID userB) {
        UUID first = userA.compareTo(userB) <= 0 ? userA : userB;
        UUID second = first.equals(userA) ? userB : userA;

        return conversationRepository
                .findByParticipantOneIdAndParticipantTwoId(first, second)
                .orElseGet(() -> create(first, second));
    }

    private ConversationEntity create(UUID first, UUID second) {
        ConversationEntity conversation = new ConversationEntity();
        conversation.setParticipantOneId(first);
        conversation.setParticipantTwoId(second);
        conversation.setCreatedAt(Instant.now(clock));
        return conversationRepository.save(conversation);
    }
}