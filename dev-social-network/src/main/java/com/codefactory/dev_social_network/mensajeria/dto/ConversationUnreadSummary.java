package com.codefactory.dev_social_network.mensajeria.dto;

import java.util.UUID;

public record ConversationUnreadSummary(UUID conversationId, UUID otherParticipantId, long unreadCount) {
}
