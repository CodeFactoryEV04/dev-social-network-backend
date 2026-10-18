package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class ConversationNotFoundException extends BusinessException {
    public ConversationNotFoundException() {
        super("CONVERSATION_NOT_FOUND", "La conversación no existe", HttpStatus.NOT_FOUND);
    }
}