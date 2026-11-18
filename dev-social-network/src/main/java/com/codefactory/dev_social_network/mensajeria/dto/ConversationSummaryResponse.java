package com.codefactory.dev_social_network.mensajeria.dto;

import java.util.UUID;

public record ConversationSummaryResponse(
        UUID conversationId,
        UUID otherParticipantId,
        String otherParticipantUsername,
        String otherParticipantName,
        long unreadCount) {
}