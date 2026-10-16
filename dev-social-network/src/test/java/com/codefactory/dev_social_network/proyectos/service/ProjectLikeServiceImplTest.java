package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectSummary;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectFinderPort;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikePort;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProjectLikeServiceImplTest {

    private final ProjectFinderPort projectFinderPort = mock(ProjectFinderPort.class);
    private final ProjectLikePort projectLikePort = mock(ProjectLikePort.class);
    private final ProjectLikeServiceImpl service =
            new ProjectLikeServiceImpl(projectFinderPort, projectLikePort);
    private final UUID usuarioId = UUID.randomUUID();

    @Test
    void darLikeExitosamente() {
        when(projectFinderPort.buscarResumenPorId(1024L))
                .thenReturn(Optional.of(new ProjectSummary(1024L, "DevConnect")));
        when(projectLikePort.registrarLike(1024L, usuarioId)).thenReturn(true);
        when(projectLikePort.contarLikes(1024L)).thenReturn(1L);

        ProjectLikeResponse response = service.likeProject(1024L, usuarioId);

        assertThat(response.projectTitle()).isEqualTo("DevConnect");
        assertThat(response.likesCount()).isEqualTo(1);
        assertThat(response.liked()).isTrue();
    }

    @Test
    void likeRepetidoNoCambiaNada() {
        when(projectFinderPort.buscarResumenPorId(1024L))
                .thenReturn(Optional.of(new ProjectSummary(1024L, "DevConnect")));
        when(projectLikePort.registrarLike(1024L, usuarioId)).thenReturn(false);

        assertThatThrownBy(() -> service.likeProject(1024L, usuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya le haz dado like a este proyecto");

        verify(projectLikePort, never()).contarLikes(anyLong());
    }

    @Test
    void proyectoInexistente() {
        when(projectFinderPort.buscarResumenPorId(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.likeProject(9999L, usuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto no existe");

        verifyNoInteractions(projectLikePort);
    }
}