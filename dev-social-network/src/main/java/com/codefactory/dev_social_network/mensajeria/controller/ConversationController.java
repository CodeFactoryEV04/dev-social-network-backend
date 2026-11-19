
package com.codefactory.dev_social_network.mensajeria.controller;

import com.codefactory.dev_social_network.mensajeria.dto.ConversationSummaryResponse;
import com.codefactory.dev_social_network.mensajeria.interfaces.AuthenticatedUserProvider;
import com.codefactory.dev_social_network.mensajeria.interfaces.ConversationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final AuthenticatedUserProvider authenticatedUser;

    public ConversationController(
            ConversationService conversationService,
            AuthenticatedUserProvider authenticatedUser) {
        this.conversationService = conversationService;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping
    public List<ConversationSummaryResponse> getMyConversations() {
        return conversationService.getMyConversations(
                authenticatedUser.currentUserId()
        );
    }
}
