package com.codefactory.dev_social_network.comentarios.service;

import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.dto.PublishedCommentResponse;
import com.codefactory.dev_social_network.comentarios.entity.ComentarioProyecto;
import com.codefactory.dev_social_network.comentarios.entity.UsuarioRef;
import com.codefactory.dev_social_network.comentarios.repository.ComentarioProyectoRepository;
import com.codefactory.dev_social_network.comentarios.repository.ProyectoRefRepository;
import com.codefactory.dev_social_network.comentarios.repository.UsuarioRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
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

    @Mock
    private UsuarioRefRepository usuarioRefRepository;

    private ProjectCommentService service;

    @BeforeEach
    void setUp() {
        service = new ProjectCommentService(
                proyectoRefRepository, comentarioProyectoRepository, usuarioRefRepository
        );
    }

    @Test
    void deberiaPublicarComentarioConMensajeYUsernameCuandoElProyectoExiste() throws Exception {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComentario("Buen uso de la arquitectura hexagonal");

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(true);
        when(comentarioProyectoRepository.save(any()))
                .thenReturn(construirComentario(idProyecto, idUsuario, "Buen uso de la arquitectura hexagonal"));
        when(usuarioRefRepository.findById(idUsuario))
                .thenReturn(Optional.of(construirUsuarioRef(idUsuario, "LuisDev")));

        PublishedCommentResponse response = service.createComment(idUsuario, idProyecto, request);

        ArgumentCaptor<ComentarioProyecto> captor = ArgumentCaptor.forClass(ComentarioProyecto.class);
        verify(comentarioProyectoRepository).save(captor.capture());
        assertThat(captor.getValue().getIdProyecto()).isEqualTo(idProyecto);
        assertThat(captor.getValue().getIdUsuario()).isEqualTo(idUsuario);

        assertThat(response.mensaje()).isEqualTo("Comentario publicado");
        assertThat(response.comentario().comentario()).isEqualTo("Buen uso de la arquitectura hexagonal");
        assertThat(response.comentario().username()).isEqualTo("LuisDev");
        assertThat(response.comentario().fechaCreacion()).isNotNull();
    }

    @Test
    void deberiaLanzarExcepcionAlComentarSiElProyectoNoExiste() {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComentario("Buen proyecto");

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(false);

        assertThatThrownBy(() -> service.createComment(idUsuario, idProyecto, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto no existe");

        verify(comentarioProyectoRepository, never()).save(any());
    }

    @Test
    void deberiaListarComentariosConUsernameCuandoElProyectoExiste() throws Exception {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();

        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(true);
        when(comentarioProyectoRepository.findByIdProyectoOrderByFechaCreacionAsc(idProyecto))
                .thenReturn(List.of(construirComentario(idProyecto, idUsuario, "Uno")));
        when(usuarioRefRepository.findAllById(any()))
                .thenReturn(List.of(construirUsuarioRef(idUsuario, "LuisDev")));

        List<CommentResponse> resultado = service.listComments(idProyecto);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).comentario()).isEqualTo("Uno");
        assertThat(resultado.get(0).username()).isEqualTo("LuisDev");
    }

    @Test
    void deberiaLanzarExcepcionAlListarSiElProyectoNoExiste() {
        UUID idProyecto = UUID.randomUUID();
        when(proyectoRefRepository.existsById(idProyecto)).thenReturn(false);

        assertThatThrownBy(() -> service.listComments(idProyecto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El proyecto no existe");
    }

    private ComentarioProyecto construirComentario(UUID idProyecto, UUID idUsuario, String texto) throws Exception {
        ComentarioProyecto comentario = new ComentarioProyecto(idProyecto, idUsuario, texto);
        Field idField = ComentarioProyecto.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(comentario, UUID.randomUUID());
        return comentario;
    }

    private UsuarioRef construirUsuarioRef(UUID id, String username) throws Exception {
        // El constructor es protected a proposito: se abre solo en el test.
        Constructor<UsuarioRef> constructor = UsuarioRef.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        UsuarioRef usuario = constructor.newInstance();

        Field idField = UsuarioRef.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(usuario, id);

        Field usernameField = UsuarioRef.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(usuario, username);
        return usuario;
    }
}