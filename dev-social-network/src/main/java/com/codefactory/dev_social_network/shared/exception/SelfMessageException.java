package com.codefactory.dev_social_network.shared.exception;

public class SelfMessageException extends RuntimeException {
    public SelfMessageException() {
        super("No puedes enviarte un mensaje a ti mismo");
    }
}