package com.codefactory.dev_social_network.mensajeria.interfaces;

import com.codefactory.dev_social_network.mensajeria.dto.ConversationSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    List<ConversationSummaryResponse> getMyConversations(UUID userId);
}