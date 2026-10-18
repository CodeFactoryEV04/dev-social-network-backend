package com.codefactory.dev_social_network.shared.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("La conversación no existe");
    }
}