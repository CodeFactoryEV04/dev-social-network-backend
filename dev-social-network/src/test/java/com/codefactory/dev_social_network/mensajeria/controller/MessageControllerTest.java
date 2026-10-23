package com.codefactory.dev_social_network.mensajeria.controller;

import com.codefactory.dev_social_network.autenticacion.service.JwtProvider;
import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.dto.SendMessageRequest;
import com.codefactory.dev_social_network.mensajeria.interfaces.AuthenticatedUserProvider;
import com.codefactory.dev_social_network.mensajeria.interfaces.MessageService;
import com.codefactory.dev_social_network.shared.config.SecurityConfig;
import com.codefactory.dev_social_network.shared.exception.ConversationAccessDeniedException;
import com.codefactory.dev_social_network.shared.exception.ConversationNotFoundException;
import com.codefactory.dev_social_network.shared.exception.RecipientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import({SecurityConfig.class, ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class MessageControllerTest {

    private static final String TOKEN = "fake-jwt-token";
    private static final Instant NOW = Instant.parse("2026-01-15T10:30:45.123456789Z");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonMapper jsonMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUser;

    @MockitoBean
    private JwtProvider jwtProvider;

    private UUID userId;
    private UUID conversationId;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        this.userId = UUID.randomUUID();
        this.conversationId = UUID.randomUUID();
        this.messageResponse = new MessageResponse(
                UUID.randomUUID(),
                conversationId,
                userId,
                "Hola, ¿cómo estás?",
                NOW
        );

        when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
        when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(userId);
    }

    private String jsonBody(String receiverEmail, String content) throws Exception {
        return jsonMapper.writeValueAsString(new SendMessageRequest(receiverEmail, content));
    }

    @Test
    void sendMessageReturnsCreatedWithTheMessage() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.sendMessage(eq(userId), any())).thenReturn(messageResponse);

        mockMvc.perform(post("/api/v1/messages")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .content(jsonBody("destino@mail.com", "Hola, ¿cómo estás?")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(messageResponse.id().toString())))
            .andExpect(jsonPath("$.conversationId", is(messageResponse.conversationId().toString())))
            .andExpect(jsonPath("$.senderId", is(messageResponse.senderId().toString())))
            .andExpect(jsonPath("$.content", is("Hola, ¿cómo estás?")))
            .andExpect(jsonPath("$.sentAt", is(messageResponse.sentAt().toString())));

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(messageService).sendMessage(eq(userId), captor.capture());
        assertThat(captor.getValue().receiverEmail()).isEqualTo("destino@mail.com");
        assertThat(captor.getValue().content()).isEqualTo("Hola, ¿cómo estás?");
    }

    @Test
    void sendMessageWithoutAuthenticationIsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                .contentType("application/json")
                .content(jsonBody("destino@mail.com", "Hola")))
            .andExpect(status().isForbidden());

        verify(messageService, never()).sendMessage(any(), any());
    }

    @Test
    void sendMessageWithBlankReceiverEmailIsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .content(jsonBody("   ", "Hola")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.message", is("Request validation failed")))
            .andExpect(jsonPath("$.details[0].field", is("receiverEmail")))
            .andExpect(jsonPath("$.details[0].message", is("El destinatario es obligatorio")));

        verify(messageService, never()).sendMessage(any(), any());
    }

    @Test
    void sendMessageWithBlankContentIsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .content(jsonBody("destino@mail.com", "  ")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.details[0].field", is("content")))
            .andExpect(jsonPath("$.details[0].message", is("El mensaje no puede estar vacío")));

        verify(messageService, never()).sendMessage(any(), any());
    }

    @Test
    void sendMessageWithTooLongContentIsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/messages")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .content(jsonBody("destino@mail.com", "x".repeat(2001))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.details[0].field", is("content")))
            .andExpect(jsonPath("$.details[0].message", is("Mensaje excede los 2000 caracteres")));

        verify(messageService, never()).sendMessage(any(), any());
    }

    @Test
    void sendMessageWhenRecipientDoesNotExistIsNotFound() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.sendMessage(eq(userId), any())).thenThrow(new RecipientNotFoundException());

        mockMvc.perform(post("/api/v1/messages")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType("application/json")
                .content(jsonBody("fantasma@mail.com", "Hola")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is("RECIPIENT_NOT_FOUND")))
            .andExpect(jsonPath("$.message", is("Usuario no existe")));
    }

    @Test
    void getConversationMessagesReturnsOkWithTheMessages() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.getConversationMessages(userId, conversationId)).thenReturn(List.of(messageResponse));

        mockMvc.perform(get("/api/v1/conversations/{conversationId}/messages", conversationId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(1)))
            .andExpect(jsonPath("$[0].id", is(messageResponse.id().toString())))
            .andExpect(jsonPath("$[0].conversationId", is(messageResponse.conversationId().toString())))
            .andExpect(jsonPath("$[0].senderId", is(messageResponse.senderId().toString())))
            .andExpect(jsonPath("$[0].content", is("Hola, ¿cómo estás?")))
            .andExpect(jsonPath("$[0].sentAt", is(messageResponse.sentAt().toString())));
    }

    @Test
    void getConversationMessagesWithoutMessagesReturnsEmptyList() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.getConversationMessages(userId, conversationId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/conversations/{conversationId}/messages", conversationId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getConversationMessagesWhenConversationDoesNotExistIsNotFound() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.getConversationMessages(userId, conversationId))
                .thenThrow(new ConversationNotFoundException());

        mockMvc.perform(get("/api/v1/conversations/{conversationId}/messages", conversationId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is("CONVERSATION_NOT_FOUND")))
            .andExpect(jsonPath("$.message", is("La conversación no existe")));
    }

    @Test
    void getConversationMessagesWhenUserIsNotParticipantIsForbidden() throws Exception {
        when(authenticatedUser.currentUserId()).thenReturn(userId);
        when(messageService.getConversationMessages(userId, conversationId))
                .thenThrow(new ConversationAccessDeniedException());

        mockMvc.perform(get("/api/v1/conversations/{conversationId}/messages", conversationId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode", is("CONVERSATION_ACCESS_DENIED")))
            .andExpect(jsonPath("$.message", is("No tienes acceso a esta conversación")));
    }
}
