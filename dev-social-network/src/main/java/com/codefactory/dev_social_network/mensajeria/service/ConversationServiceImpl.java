
package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.dto.ConversationSummaryResponse;
import com.codefactory.dev_social_network.mensajeria.interfaces.ConversationService;
import com.codefactory.dev_social_network.mensajeria.interfaces.ConversationSummaryReaderPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationSummaryReaderPort summaryReader;

    public ConversationServiceImpl(ConversationSummaryReaderPort summaryReader) {
        this.summaryReader = summaryReader;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getMyConversations(UUID userId) {
        return summaryReader.findByParticipant(userId)
                .stream()
                .map(summary -> new ConversationSummaryResponse(
                        summary.conversationId(),
                        summary.otherParticipantId(),
                        summary.unreadCount()
                ))
                .toList();
    }
}
