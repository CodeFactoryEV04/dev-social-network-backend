package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(MessageEntity message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSenderId(),
                message.getContent(),
                message.getSentAt());
    }
}