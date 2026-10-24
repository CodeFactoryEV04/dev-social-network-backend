package com.codefactory.dev_social_network.proyectos.repository;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.ProjectLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU010 - ProjectLikeAdapter")
public class ProjectLikeAdapterTest {
    private static final Long PROYECTO_ID = 1024L;
    private static final UUID LUISDEV_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private ProjectLikeRepository projectLikeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectLikeAdapter adapter;

    @Captor
    private ArgumentCaptor<ProjectLike> likeCaptor;

    private Project devconnect;

    @BeforeEach
    void setUp() {
        devconnect = new Project();
        devconnect.setId(PROYECTO_ID);
        devconnect.setTitle("DevConnect");
        devconnect.setRepositoryUrl("https://github.com/luisdev/devconnect");
    }

    @Test
    @DisplayName("A01 · Si el like ya existe devuelve false y no guarda nada")
    void registrarLike_yaExiste_retornaFalseSinGuardar() {
        when(projectLikeRepository.existsByProject_IdAndUsuarioId(PROYECTO_ID, LUISDEV_ID)).thenReturn(true);

        boolean creado = adapter.registrarLike(PROYECTO_ID, LUISDEV_ID);

        assertThat(creado).isFalse();
        verify(projectRepository, never()).getReferenceById(anyLong());
        verify(projectLikeRepository, never()).saveAndFlush(any(ProjectLike.class));
    }

    @Test
    @DisplayName("A02 · Like nuevo: guarda el ProjectLike correcto y devuelve true")
    void registrarLike_noExiste_guardaYRetornaTrue() {
        when(projectLikeRepository.existsByProject_IdAndUsuarioId(PROYECTO_ID, LUISDEV_ID)).thenReturn(false);
        when(projectRepository.getReferenceById(PROYECTO_ID)).thenReturn(devconnect);

        boolean creado = adapter.registrarLike(PROYECTO_ID, LUISDEV_ID);

        assertThat(creado).isTrue();
        verify(projectLikeRepository).saveAndFlush(likeCaptor.capture());
        ProjectLike guardado = likeCaptor.getValue();
        assertThat(guardado.getProject()).isSameAs(devconnect);
        assertThat(guardado.getUsuarioId()).isEqualTo(LUISDEV_ID);
        assertThat(guardado.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("A03 · Concurrencia: si la BD rechaza el duplicado devuelve false")
    void registrarLike_violacionRestriccionUnica_retornaFalse() {
        when(projectLikeRepository.existsByProject_IdAndUsuarioId(PROYECTO_ID, LUISDEV_ID)).thenReturn(false);
        when(projectRepository.getReferenceById(PROYECTO_ID)).thenReturn(devconnect);
        when(projectLikeRepository.saveAndFlush(any(ProjectLike.class)))
                .thenThrow(new DataIntegrityViolationException("uk_project_like_project_user"));

        boolean creado = adapter.registrarLike(PROYECTO_ID, LUISDEV_ID);

        assertThat(creado).isFalse();
    }

    @Test
    @DisplayName("A04 · contarLikes delega en el repositorio")
    void contarLikes_delegaEnRepositorio() {
        when(projectLikeRepository.countByProject_Id(PROYECTO_ID)).thenReturn(3L);

        assertThat(adapter.contarLikes(PROYECTO_ID)).isEqualTo(3L);
    }

    @Test
    @DisplayName("A05 · listarPorUsuario mapea la entidad al DTO")
    void listarPorUsuario_conLikes_mapeaCampos() {
        ProjectLike like = new ProjectLike(devconnect, LUISDEV_ID);
        when(projectLikeRepository.findAllByUsuarioIdWithProject(LUISDEV_ID)).thenReturn(List.of(like));

        List<LikedProjectResponse> resultado = adapter.listarPorUsuario(LUISDEV_ID);

        assertThat(resultado).containsExactly(new LikedProjectResponse(
                PROYECTO_ID, "DevConnect", "https://github.com/luisdev/devconnect", like.getCreatedAt()));
    }

    @Test
    @DisplayName("A06 · listarPorUsuario sin likes devuelve lista vacía")
    void listarPorUsuario_sinLikes_retornaVacia() {
        when(projectLikeRepository.findAllByUsuarioIdWithProject(LUISDEV_ID)).thenReturn(List.of());

        assertThat(adapter.listarPorUsuario(LUISDEV_ID)).isEmpty();
    }
}
