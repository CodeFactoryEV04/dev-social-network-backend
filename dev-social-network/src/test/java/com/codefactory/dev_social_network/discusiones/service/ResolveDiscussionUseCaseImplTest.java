package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveDiscussionUseCaseImplTest {

    @Mock
    private DiscussionFinderPort discussionFinderPort;

    @Mock
    private DiscussionResolverPort discussionResolverPort;

    private ResolveDiscussionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        List<DiscussionResolutionRule> rules = List.of(
                new OnlyAuthorCanResolveRule(),
                new DiscussionNotAlreadyResolvedRule()
        );
        useCase = new ResolveDiscussionUseCaseImpl(discussionFinderPort, discussionResolverPort, rules);
    }

    @Test
    void deberiaMarcarComoResueltaCuandoElAutorLaResuelveYEstaAbierta() throws Exception {
        UUID autorId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        Discussion discussion = construirDiscusion(autorId, DiscussionStatus.ABIERTA);

        when(discussionFinderPort.findById(discussionId)).thenReturn(Optional.of(discussion));
        when(discussionResolverPort.markAsResolved(discussionId)).thenReturn(true);

        ResolveDiscussionResponse response = useCase.resolve(autorId, discussionId);

        assertThat(response.status()).isEqualTo(DiscussionStatus.RESUELTA);
        assertThat(response.message()).isEqualTo("La discusión ha sido marcada como resuelta");
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaDiscusionYaEstaResuelta() throws Exception {
        UUID autorId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        Discussion discussion = construirDiscusion(autorId, DiscussionStatus.RESUELTA);

        when(discussionFinderPort.findById(discussionId)).thenReturn(Optional.of(discussion));

        assertThatThrownBy(() -> useCase.resolve(autorId, discussionId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Está discusión ya ha sido resuelta");

        verify(discussionResolverPort, never()).markAsResolved(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoQuienResuelveNoEsElAutor() throws Exception {
        UUID autorId = UUID.randomUUID();
        UUID otroUsuarioId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        Discussion discussion = construirDiscusion(autorId, DiscussionStatus.ABIERTA);

        when(discussionFinderPort.findById(discussionId)).thenReturn(Optional.of(discussion));

        assertThatThrownBy(() -> useCase.resolve(otroUsuarioId, discussionId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Solo el autor puede marcar como resuelta la discusión");

        verify(discussionResolverPort, never()).markAsResolved(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaDiscusionNoExiste() {
        UUID autorId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();

        when(discussionFinderPort.findById(discussionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.resolve(autorId, discussionId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Está discusión no existe");
    }

    private Discussion construirDiscusion(UUID autorId, DiscussionStatus estado) throws Exception {
        Discussion discussion = new Discussion(autorId, UUID.randomUUID(), "Titulo", "Contenido de prueba");
        Field estadoField = Discussion.class.getDeclaredField("estado");
        estadoField.setAccessible(true);
        estadoField.set(discussion, estado);
        return discussion;
    }
}