package com.codefactory.dev_social_network.Mensajeria.dto;

import java.util.UUID;
import java.time.LocalDateTime;

public record MessageResponse(UUID id, UUID senderId, UUID conversationId, String content, LocalDateTime sentAt) {}