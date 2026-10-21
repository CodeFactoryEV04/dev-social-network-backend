package com.codefactory.dev_social_network.discusiones.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.repository.DiscussionRepository;
import com.codefactory.dev_social_network.proyectos.entity.Technology;
import com.codefactory.dev_social_network.proyectos.repository.TechnologyRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;
    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private DiscussionService service;

    private final UUID usuarioId = UUID.randomUUID();
    private final Long tecnologiaId = 1L;

    private Technology tecnologia(Long id, String name) {
        Technology technology = new Technology();
        technology.setId(id);
        technology.setName(name);
        technology.setType("LENGUAJE");
        return technology;
    }

    private CreateDiscussionRequest peticion(String title, String content, Long technologyId) {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setTechnologyId(technologyId);
        return request;
    }

    private CreateDiscussionRequest peticionValida() {
        return peticion("Spring Security", "Como proteger un endpoint con JWT", tecnologiaId);
    }

    private void tecnologiaEncontrada() {
        when(technologyRepository.findById(tecnologiaId))
                .thenReturn(Optional.of(tecnologia(tecnologiaId, "Java")));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invoc -> invoc.getArgument(0));
    }

    // ------------------------------------------------------------------ creacion

    @Test
    @DisplayName("crea la discusion y devuelve la respuesta mapeada")
    void creaLaDiscusion() {
        // Arrange
        tecnologiaEncontrada();

        // Act
        DiscussionResponse respuesta = service.createDiscussion(usuarioId, peticionValida());

        // Assert
        assertThat(respuesta.getUserId()).isEqualTo(usuarioId);
        assertThat(respuesta.getTitle()).isEqualTo("Spring Security");
        assertThat(respuesta.getContent()).isEqualTo("Como proteger un endpoint con JWT");
        assertThat(respuesta.getStatus()).isEqualTo(DiscussionStatus.OPEN);
        assertThat(respuesta.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("asocia la discusion al usuario autenticado como autor")
    void asociaElUsuarioAutenticadoComoAutor() {
        // Arrange
        tecnologiaEncontrada();

        // Act
        service.createDiscussion(usuarioId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(usuarioId);
    }

    @Test
    @DisplayName("clasifica la discusion bajo la tecnologia indicada")
    void clasificaLaDiscusionBajoLaTecnologiaIndicada() {
        // Arrange
        tecnologiaEncontrada();

        // Act
        DiscussionResponse respuesta = service.createDiscussion(usuarioId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getTechnologyId()).isEqualTo(tecnologiaId);
        assertThat(respuesta.getTechnology()).isEqualTo("Java");
    }

    @Test
    @DisplayName("deja la discusion disponible de inmediato devolviendo la instancia ya persistida")
    void devuelveLaDiscusionPersistida() {
        // Arrange
        when(technologyRepository.findById(tecnologiaId))
                .thenReturn(Optional.of(tecnologia(tecnologiaId, "Java")));
        Discussion guardada = new Discussion(
                usuarioId, tecnologiaId, "Titulo persistido", "Contenido persistido");
        when(discussionRepository.save(any(Discussion.class))).thenReturn(guardada);

        // Act
        DiscussionResponse respuesta = service.createDiscussion(usuarioId, peticionValida());

        // Assert
        assertThat(respuesta.getTitle()).isEqualTo("Titulo persistido");
        assertThat(respuesta.getContent()).isEqualTo("Contenido persistido");
    }

    @Test
    @DisplayName("persiste la discusion con estado abierto y fechas de creacion y actualizacion")
    void persisteConEstadoAbiertoYFechas() {
        // Arrange
        tecnologiaEncontrada();

        // Act
        service.createDiscussion(usuarioId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(DiscussionStatus.OPEN);
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
        assertThat(captor.getValue().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("busca la tecnologia por el identificador recibido en la peticion")
    void buscaLaTecnologiaPorElIdentificadorRecibido() {
        // Arrange
        when(technologyRepository.findById(7L)).thenReturn(Optional.of(tecnologia(7L, "React")));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invoc -> invoc.getArgument(0));

        // Act
        DiscussionResponse respuesta = service.createDiscussion(
                usuarioId, peticion("Hooks y estado global", "useContext o Redux", 7L));

        // Assert
        assertThat(respuesta.getTechnology()).isEqualTo("React");
        verify(technologyRepository).findById(7L);
    }

    // --------------------------------------------------------------- tecnologia

    @Test
    @DisplayName("falla con 400 e INVALID_TECHNOLOGY si la tecnologia indicada no existe")
    void fallaSiLaTecnologiaNoExiste() {
        // Arrange
        when(technologyRepository.findById(tecnologiaId)).thenReturn(Optional.empty());

        // Act
        BusinessException error = catchThrowableOfType(
                () -> service.createDiscussion(usuarioId, peticionValida()),
                BusinessException.class);

        // Assert
        assertThat(error).isNotNull();
        assertThat(error.getErrorCode()).isEqualTo("INVALID_TECHNOLOGY");
        assertThat(error.getMessage()).isEqualTo("Technology does not exist");
        assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("no guarda ninguna discusion si la tecnologia no existe")
    void noGuardaDiscusionSiLaTecnologiaNoExiste() {
        // Arrange
        when(technologyRepository.findById(tecnologiaId)).thenReturn(Optional.empty());

        // Act
        catchThrowableOfType(
                () -> service.createDiscussion(usuarioId, peticionValida()),
                BusinessException.class);

        // Assert
        verify(discussionRepository, never()).save(any());
    }
}
