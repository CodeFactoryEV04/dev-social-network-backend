package com.codefactory.dev_social_network.comentarios.service;
 
import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.entity.ComentarioProyecto;
import com.codefactory.dev_social_network.comentarios.entity.ProyectoRef;
import com.codefactory.dev_social_network.comentarios.repository.ComentarioProyectoRepository;
import com.codefactory.dev_social_network.comentarios.repository.ProyectoRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCommentServiceTest {

    @Mock
    private ProyectoRefRepository proyectoRefRepository;

    @Mock
    private ComentarioProyectoRepository comentarioProyectoRepository;

    private ProjectCommentService service;

    @BeforeEach
    void setUp() {
        service = new ProjectCommentService(proyectoRefRepository, comentarioProyectoRepository);
    }

    @Test
    void deberiaCrearComentarioCuandoElProyectoExiste() throws Exception {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComentario("Excelente trabajo");

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(true);

        ComentarioProyecto comentarioGuardado = construirComentario(idProyecto, idUsuario, "Excelente trabajo");
        when(comentarioProyectoRepository.save(any())).thenReturn(comentarioGuardado);

        CommentResponse response = service.createComment(idUsuario, idProyecto, request);

        ArgumentCaptor<ComentarioProyecto> captor = ArgumentCaptor.forClass(ComentarioProyecto.class);
        verify(comentarioProyectoRepository).save(captor.capture());
        assertThat(captor.getValue().getIdProyecto()).isEqualTo(idProyecto);
        assertThat(captor.getValue().getIdUsuario()).isEqualTo(idUsuario);
        assertThat(captor.getValue().getComentario()).isEqualTo("Excelente trabajo");

        assertThat(response.comentario()).isEqualTo("Excelente trabajo");
        assertThat(response.idUsuario()).isEqualTo(idUsuario);
    }

    @Test
    void deberiaLanzarExcepcionAlCrearComentarioSiElProyectoNoExiste() {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComentario("Comentario cualquiera");

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(false);

        assertThatThrownBy(() -> service.createComment(idUsuario, idProyecto, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto indicado no existe");

        verify(comentarioProyectoRepository, never()).save(any());
    }

    @Test
    void deberiaListarComentariosCuandoElProyectoExiste() throws Exception {
        UUID idProyecto = UUID.randomUUID();
        ComentarioProyecto comentario = construirComentario(idProyecto, UUID.randomUUID(), "Uno");

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(true);
        when(comentarioProyectoRepository.findByIdProyectoOrderByFechaCreacionAsc(idProyecto))
                .thenReturn(List.of(comentario));

        List<CommentResponse> resultado = service.listComments(idProyecto);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).comentario()).isEqualTo("Uno");
    }

    @Test
    void deberiaLanzarExcepcionAlListarSiElProyectoNoExiste() {
        UUID idProyecto = UUID.randomUUID();
        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(false);

        assertThatThrownBy(() -> service.listComments(idProyecto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto indicado no existe");
    }

    private ComentarioProyecto construirComentario(UUID idProyecto, UUID idUsuario, String texto) throws Exception {
        ComentarioProyecto comentario = new ComentarioProyecto(idProyecto, idUsuario, texto);
        Field idField = ComentarioProyecto.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(comentario, UUID.randomUUID());
        return comentario;
    }
}