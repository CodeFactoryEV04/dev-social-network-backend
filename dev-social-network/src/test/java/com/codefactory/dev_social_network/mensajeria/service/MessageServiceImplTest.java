package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.dto.SendMessageRequest;
import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import com.codefactory.dev_social_network.mensajeria.entity.MessageEntity;
import com.codefactory.dev_social_network.mensajeria.repository.ConversationRepository;
import com.codefactory.dev_social_network.mensajeria.repository.MessageRepository;
import com.codefactory.dev_social_network.shared.exception.ConversationAccessDeniedException;
import com.codefactory.dev_social_network.shared.exception.ConversationNotFoundException;
import com.codefactory.dev_social_network.shared.exception.RecipientNotFoundException;
import com.codefactory.dev_social_network.shared.exception.SelfMessageException;
import com.codefactory.dev_social_network.usuarios.dto.UsuarioDTO;
import com.codefactory.dev_social_network.usuarios.interfaces.UsuarioQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:30:45.123456789Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private UsuarioQueryService usuarioQueryService;

    @Mock
    private ConversationResolver conversationResolver;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    private MessageServiceImpl service;

    private UUID senderId;
    private UUID receiverId;
    private ConversationEntity conversation;
    private UUID messageId;

    @BeforeEach
    void setUp() {
        // El mapper real: es una funcion pura y asi tambien queda cubierto.
        service = new MessageServiceImpl(
                usuarioQueryService,
                conversationResolver,
                conversationRepository,
                messageRepository,
                new MessageMapper(),
                FIXED_CLOCK
        );

        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        conversation = conversationWithId(UUID.randomUUID(), senderId, receiverId);
    }

    @Test
    void deberiaEnviarYGuardarElMensajeCuandoElDestinatarioExiste() {
        SendMessageRequest request = new SendMessageRequest("destino@mail.com", "Hola, ¿cómo estás?");

        when(usuarioQueryService.buscarPorEmail("destino@mail.com"))
                .thenReturn(Optional.of(new UsuarioDTO(receiverId, "destino@mail.com")));
        when(conversationResolver.resolve(senderId, receiverId)).thenReturn(conversation);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity saved = invocation.getArgument(0);
            setId(saved, messageId);
            return saved;
        });

        MessageResponse response = service.sendMessage(senderId, request);

        assertThat(response.id()).isEqualTo(messageId);
        assertThat(response.conversationId()).isEqualTo(conversation.getId());
        assertThat(response.senderId()).isEqualTo(senderId);
        assertThat(response.content()).isEqualTo("Hola, ¿cómo estás?");
        assertThat(response.sentAt()).isEqualTo(NOW);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());
        assertThat(captor.getValue().getConversation()).isSameAs(conversation);
        assertThat(captor.getValue().getSenderId()).isEqualTo(senderId);
        assertThat(captor.getValue().getContent()).isEqualTo("Hola, ¿cómo estás?");
        assertThat(captor.getValue().getSentAt()).isEqualTo(NOW);
    }

    @Test
    void deberiaLanzarExcepcionSiElDestinatarioNoExiste() {
        SendMessageRequest request = new SendMessageRequest("fantasma@mail.com", "Hola");

        when(usuarioQueryService.buscarPorEmail("fantasma@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendMessage(senderId, request))
                .isInstanceOf(RecipientNotFoundException.class)
                .hasMessage("Usuario no existe");

        verify(messageRepository, never()).save(any());
        verify(conversationResolver, never()).resolve(any(), any());
    }

    @Test
    void deberiaLanzarExcepcionSiElRemitenteSeEnviaAMismo() {
        SendMessageRequest request = new SendMessageRequest("yo@mail.com", "Hola");

        when(usuarioQueryService.buscarPorEmail("yo@mail.com"))
                .thenReturn(Optional.of(new UsuarioDTO(senderId, "yo@mail.com")));

        assertThatThrownBy(() -> service.sendMessage(senderId, request))
                .isInstanceOf(SelfMessageException.class)
                .hasMessage("No puedes enviarte un mensaje a ti mismo");

        verify(conversationResolver, never()).resolve(any(), any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void deberiaListarLosMensajesDeLaConversacionCuandoElUsuarioEsParticipante() {
        UUID conversationId = UUID.randomUUID();
        UUID otroUsuarioId = UUID.randomUUID();
        ConversationEntity conversacion = conversationWithId(conversationId, senderId, otroUsuarioId);

        MessageEntity primero = messageWithId(UUID.randomUUID(), conversacion, senderId, "Primero", NOW);
        MessageEntity segundo = messageWithId(UUID.randomUUID(), conversacion, otroUsuarioId, "Segundo", NOW.plusSeconds(60));

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversacion));
        when(messageRepository.findByConversation_IdOrderBySentAtAsc(conversationId)).thenReturn(List.of(primero, segundo));

        List<MessageResponse> mensajes = service.getConversationMessages(senderId, conversationId);

        assertThat(mensajes).hasSize(2);
        assertThat(mensajes.get(0).content()).isEqualTo("Primero");
        assertThat(mensajes.get(0).senderId()).isEqualTo(senderId);
        assertThat(mensajes.get(0).conversationId()).isEqualTo(conversationId);
        assertThat(mensajes.get(1).content()).isEqualTo("Segundo");
        assertThat(mensajes.get(1).senderId()).isEqualTo(otroUsuarioId);
    }

    @Test
    void deberiaListarLosMensajesCuandoElUsuarioEsElSegundoParticipante() {
        UUID conversationId = UUID.randomUUID();
        UUID otroUsuarioId = UUID.randomUUID();
        ConversationEntity conversacion = conversationWithId(conversationId, otroUsuarioId, senderId);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversacion));
        when(messageRepository.findByConversation_IdOrderBySentAtAsc(conversationId)).thenReturn(List.of());

        List<MessageResponse> mensajes = service.getConversationMessages(senderId, conversationId);

        assertThat(mensajes).isEmpty();
    }

    @Test
    void deberiaLanzarExcepcionSiLaConversacionNoExiste() {
        UUID conversationId = UUID.randomUUID();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getConversationMessages(senderId, conversationId))
                .isInstanceOf(ConversationNotFoundException.class)
                .hasMessage("La conversación no existe");

        verify(messageRepository, never()).findByConversation_IdOrderBySentAtAsc(any());
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoEsParticipante() {
        UUID conversationId = UUID.randomUUID();
        ConversationEntity conversacion = conversationWithId(
                conversationId, UUID.randomUUID(), UUID.randomUUID());

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversacion));

        assertThatThrownBy(() -> service.getConversationMessages(senderId, conversationId))
                .isInstanceOf(ConversationAccessDeniedException.class)
                .hasMessage("No tienes acceso a esta conversación");

        verify(messageRepository, never()).findByConversation_IdOrderBySentAtAsc(any());
    }

    @Test
    void deberiaResolverLaConversacionEntreElRemitenteYElDestinatario() {
        SendMessageRequest request = new SendMessageRequest("destino@mail.com", "Hola");

        when(usuarioQueryService.buscarPorEmail("destino@mail.com"))
                .thenReturn(Optional.of(new UsuarioDTO(receiverId, "destino@mail.com")));
        when(conversationResolver.resolve(senderId, receiverId)).thenReturn(conversation);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity saved = invocation.getArgument(0);
            setId(saved, messageId);
            return saved;
        });

        service.sendMessage(senderId, request);

        verify(conversationResolver).resolve(eq(senderId), eq(receiverId));
    }

    @Test
    void deberiaReutilizarLaMismaConversacionEnMensajesSucesivos() {
        SendMessageRequest primero = new SendMessageRequest("destino@mail.com", "Hola");
        SendMessageRequest segundo = new SendMessageRequest("destino@mail.com", "¿Tienes tiempo esta semana?");

        // Resolver real: la primera llamada crea la conversación y la segunda debe reutilizarla.
        MessageServiceImpl serviceConResolverReal = new MessageServiceImpl(
                usuarioQueryService,
                new ConversationResolver(conversationRepository, FIXED_CLOCK),
                conversationRepository,
                messageRepository,
                new MessageMapper(),
                FIXED_CLOCK
        );

        when(usuarioQueryService.buscarPorEmail("destino@mail.com"))
                .thenReturn(Optional.of(new UsuarioDTO(receiverId, "destino@mail.com")));
        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(any(), any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any(ConversationEntity.class))).thenReturn(conversation);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity saved = invocation.getArgument(0);
            setId(saved, messageId);
            return saved;
        });

        MessageResponse primeraRespuesta = serviceConResolverReal.sendMessage(senderId, primero);
        MessageResponse segundaRespuesta = serviceConResolverReal.sendMessage(senderId, segundo);

        assertThat(primeraRespuesta.conversationId()).isEqualTo(conversation.getId());
        assertThat(segundaRespuesta.conversationId()).isEqualTo(conversation.getId());

        ArgumentCaptor<MessageEntity> mensajes = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository, times(2)).save(mensajes.capture());
        assertThat(mensajes.getAllValues())
                .extracting(MessageEntity::getContent)
                .containsExactly("Hola", "¿Tienes tiempo esta semana?");
        assertThat(mensajes.getAllValues())
                .extracting(MessageEntity::getConversation)
                .containsOnly(conversation);
        verify(conversationRepository, times(1)).save(any(ConversationEntity.class));
    }

    private ConversationEntity conversationWithId(UUID id, UUID participantOne, UUID participantTwo) {
        ConversationEntity conversation = new ConversationEntity();
        setId(conversation, id);
        conversation.setParticipantOneId(participantOne);
        conversation.setParticipantTwoId(participantTwo);
        conversation.setCreatedAt(NOW);
        return conversation;
    }

    private MessageEntity messageWithId(UUID id, ConversationEntity conversation, UUID sender,
                                        String content, Instant sentAt) {
        MessageEntity message = new MessageEntity();
        setId(message, id);
        message.setConversation(conversation);
        message.setSenderId(sender);
        message.setContent(content);
        message.setSentAt(sentAt);
        return message;
    }

    private void setId(Object entity, UUID id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo asignar el id a " + entity.getClass().getSimpleName(), e);
        }
    }
}
