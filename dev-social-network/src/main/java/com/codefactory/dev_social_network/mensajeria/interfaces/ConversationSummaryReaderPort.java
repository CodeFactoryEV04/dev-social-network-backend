package com.codefactory.dev_social_network.mensajeria.interfaces;

import com.codefactory.dev_social_network.mensajeria.dto.ConversationUnreadSummary;

import java.util.List;
import java.util.UUID;

public interface ConversationSummaryReaderPort {

    /**
     * Conversaciones del usuario, cada una con los mensajes que recibió y aún no leyó.
     * Los mensajes enviados por el propio usuario nunca se cuentan.
     */
    List<ConversationUnreadSummary> findByParticipant(UUID userId);
}