package com.codefactory.dev_social_network.shared.exception;

public class ConversationAccessDeniedException extends RuntimeException {
    public ConversationAccessDeniedException() {
        super("No tienes acceso a esta conversación");
    }
}