package com.codefactory.dev_social_network.shared.exception;

import org.springframework.http.HttpStatus;

public class ConversationAccessDeniedException extends BusinessException {
    public ConversationAccessDeniedException() {
        super("CONVERSATION_ACCESS_DENIED", "No tienes acceso a esta conversación", HttpStatus.FORBIDDEN);
    }
}