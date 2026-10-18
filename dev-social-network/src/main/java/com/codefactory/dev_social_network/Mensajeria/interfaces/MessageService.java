package com.codefactory.dev_social_network.Mensajeria.interfaces;

import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID senderId, SendMessageRequest request);

    List<MessageResponse> getConversationMessages(UUID requesterId, UUID conversationId);
}