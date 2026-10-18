package com.codefactory.dev_social_network.shared.exception;

public class RecipientNotFoundException extends RuntimeException {
    public RecipientNotFoundException() {
        super("Usuario no existe");
    }
}