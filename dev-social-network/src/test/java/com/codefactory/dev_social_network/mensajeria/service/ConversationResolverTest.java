package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import com.codefactory.dev_social_network.mensajeria.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationResolverTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:30:45.123456789Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    /** UUIDs deterministas: "…001" es menor que "…002" segun UUID.compareTo. */
    private static final UUID MENOR = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MAYOR = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock
    private ConversationRepository conversationRepository;

    private ConversationResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ConversationResolver(conversationRepository, FIXED_CLOCK);
    }

    @Test
    void deberiaDevolverLaConversacionExistente() {
        ConversationEntity existente = new ConversationEntity();
        existente.setParticipantOneId(MENOR);
        existente.setParticipantTwoId(MAYOR);

        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(MENOR, MAYOR))
                .thenReturn(Optional.of(existente));

        ConversationEntity result = resolver.resolve(MENOR, MAYOR);

        assertThat(result).isSameAs(existente);
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void deberiaCrearYGuardarUnaConversacionNuevaSiNoExiste() {
        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(MENOR, MAYOR))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConversationEntity result = resolver.resolve(MENOR, MAYOR);

        ArgumentCaptor<ConversationEntity> captor = ArgumentCaptor.forClass(ConversationEntity.class);
        verify(conversationRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(result);
        assertThat(result.getParticipantOneId()).isEqualTo(MENOR);
        assertThat(result.getParticipantTwoId()).isEqualTo(MAYOR);
        assertThat(result.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void deberiaAplicarElOrdenCanonicoCuandoElPrimerUsuarioEsMayor() {
        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(MENOR, MAYOR))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConversationEntity result = resolver.resolve(MAYOR, MENOR);

        verify(conversationRepository).findByParticipantOneIdAndParticipantTwoId(MENOR, MAYOR);
        assertThat(result.getParticipantOneId()).isEqualTo(MENOR);
        assertThat(result.getParticipantTwoId()).isEqualTo(MAYOR);
    }

    @Test
    void deberiaBuscarLaConversacionConLosParticipantesOrdenados() {
        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(eq(MENOR), eq(MAYOR)))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        resolver.resolve(MENOR, MAYOR);

        verify(conversationRepository).findByParticipantOneIdAndParticipantTwoId(MENOR, MAYOR);
    }
}
