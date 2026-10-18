package com.codefactory.dev_social_network.mensajeria.controller;

import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.dto.SendMessageRequest;
import com.codefactory.dev_social_network.mensajeria.interfaces.AuthenticatedUserProvider;
import com.codefactory.dev_social_network.mensajeria.interfaces.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;
    private final AuthenticatedUserProvider authenticatedUser;

    public MessageController(MessageService messageService,
                             AuthenticatedUserProvider authenticatedUser) {
        this.messageService = messageService;
        this.authenticatedUser = authenticatedUser;
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(authenticatedUser.currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> getConversationMessages(@PathVariable UUID conversationId) {
        return messageService.getConversationMessages(authenticatedUser.currentUserId(), conversationId);
    }
}