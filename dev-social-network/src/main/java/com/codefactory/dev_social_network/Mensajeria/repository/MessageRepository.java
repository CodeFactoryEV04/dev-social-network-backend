package com.codefactory.dev_social_network.Mensajeria.repository;

import com.codefactory.dev_social_network.mensajeria.entity.MessageEntity;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends Repository<MessageEntity, UUID> {

    MessageEntity save(MessageEntity message);

    List<MessageEntity> findByConversation_IdOrderBySentAtAsc(UUID conversationId);
}