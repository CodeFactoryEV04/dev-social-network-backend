package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.ProjectLike;
import com.codefactory.dev_social_network.proyectos.repository.ProjectLikeRepository;
import com.codefactory.dev_social_network.proyectos.repository.ProjectRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectLikeServiceImplTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectLikeRepository likeRepository = mock(ProjectLikeRepository.class);
    private final ProjectLikeServiceImpl service =
            new ProjectLikeServiceImpl(projectRepository, likeRepository);
    private final UUID usuarioId = UUID.randomUUID();

    private Project devConnect() {
        Project p = new Project();
        p.setId(1024L);
        p.setTitle("DevConnect");
        return p;
    }

    @Test
    void darLikeExitosamente() {
        when(projectRepository.findById(1024L)).thenReturn(Optional.of(devConnect()));
        when(likeRepository.existsByProject_IdAndUsuarioId(1024L, usuarioId)).thenReturn(false);
        when(likeRepository.countByProject_Id(1024L)).thenReturn(1L);

        ProjectLikeResponse response = service.likeProject(1024L, usuarioId);

        assertThat(response.likesCount()).isEqualTo(1);
        assertThat(response.liked()).isTrue();
        verify(likeRepository).saveAndFlush(any(ProjectLike.class));
    }

    @Test
    void likeRepetidoNoCambiaNada() {
        when(projectRepository.findById(1024L)).thenReturn(Optional.of(devConnect()));
        when(likeRepository.existsByProject_IdAndUsuarioId(1024L, usuarioId)).thenReturn(true);

        assertThatThrownBy(() -> service.likeProject(1024L, usuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya le haz dado like a este proyecto");

        verify(likeRepository, never()).saveAndFlush(any());
    }

    @Test
    void proyectoInexistente() {
        when(projectRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.likeProject(9999L, usuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto no existe");

        verify(likeRepository, never()).saveAndFlush(any());
    }
}