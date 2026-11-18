package com.codefactory.dev_social_network.mensajeria.interfaces;

import java.time.Instant;
import java.util.UUID;

public interface ConversationReadPort {

    void markAsRead(UUID readerId, UUID conversationId, Instant readAt);
}