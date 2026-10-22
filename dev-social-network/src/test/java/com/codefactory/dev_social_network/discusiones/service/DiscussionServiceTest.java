package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.entity.TecnologiaRef;
import com.codefactory.dev_social_network.discusiones.repository.DiscussionRepository;
import com.codefactory.dev_social_network.discusiones.repository.TecnologiaRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private TecnologiaRefRepository tecnologiaRefRepository;

    private DiscussionService discussionService;

    private final UUID autorId = UUID.randomUUID();
    private final UUID tecnologiaId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        discussionService = new DiscussionService(discussionRepository, tecnologiaRefRepository);
    }

    @Test
    void shouldCreateDiscussionWhenTechnologyExists() throws Exception {

        UUID idTecnologia = UUID.randomUUID();
        TecnologiaRef tecnologia = newTecnologiaRef(idTecnologia, "Spring Boot");

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Como configurar Spring Security con JWT?");
        request.setContenido("Me sale un 403 incluso con un token valido.");
        request.setIdTecnologia(idTecnologia);

        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.of(tecnologia));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DiscussionResponse response = discussionService.createDiscussion(autorId, request);

        assertThat(response.getTecnologia()).isEqualTo("Spring Boot");
        assertThat(response.getTitulo()).isEqualTo(request.getTitulo());
        assertThat(response.getContenido()).isEqualTo(request.getContenido());
        assertThat(response.getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
        assertThat(response.getAutorId()).isEqualTo(autorId);

        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());

        Discussion saved = captor.getValue();
        assertThat(saved.getAutorId()).isEqualTo(autorId);
        assertThat(saved.getIdTecnologia()).isEqualTo(idTecnologia);
    }

    @Test
    void shouldThrowBusinessExceptionWhenTechnologyDoesNotExist() {

        UUID idTecnologia = UUID.randomUUID();

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Como configurar Spring Security con JWT?");
        request.setContenido("Me sale un 403 incluso con un token valido.");
        request.setIdTecnologia(idTecnologia);

        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discussionService.createDiscussion(autorId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("La tecnologia indicada no existe");

        verify(discussionRepository, never()).save(any());
    }

    // ------------------------------------------------------------------ creacion

    @Test
    @DisplayName("crea la discusion y devuelve la respuesta mapeada")
    void creaLaDiscusion() throws Exception {
        // Arrange
        tecnologiaEncontrada();

        // Act
        DiscussionResponse respuesta = discussionService.createDiscussion(autorId, peticionValida());

        // Assert
        assertThat(respuesta.getAutorId()).isEqualTo(autorId);
        assertThat(respuesta.getTitulo()).isEqualTo("Spring Security");
        assertThat(respuesta.getContenido()).isEqualTo("Como proteger un endpoint con JWT");
        assertThat(respuesta.getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
        assertThat(respuesta.getFechaCreacion()).isNotNull();
    }

    @Test
    @DisplayName("asocia la discusion al usuario autenticado como autor")
    void asociaElUsuarioAutenticadoComoAutor() throws Exception {
        // Arrange
        tecnologiaEncontrada();

        // Act
        discussionService.createDiscussion(autorId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getAutorId()).isEqualTo(autorId);
    }

    @Test
    @DisplayName("clasifica la discusion bajo la tecnologia indicada")
    void clasificaLaDiscusionBajoLaTecnologiaIndicada() throws Exception {
        // Arrange
        tecnologiaEncontrada();

        // Act
        DiscussionResponse respuesta = discussionService.createDiscussion(autorId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getIdTecnologia()).isEqualTo(tecnologiaId);
        assertThat(respuesta.getTecnologia()).isEqualTo("Java");
    }

    @Test
    @DisplayName("deja la discusion disponible de inmediato devolviendo la instancia ya persistida")
    void devuelveLaDiscusionPersistida() throws Exception {
        // Arrange
        when(tecnologiaRefRepository.findById(tecnologiaId))
                .thenReturn(Optional.of(newTecnologiaRef(tecnologiaId, "Java")));
        Discussion guardada = new Discussion(
                autorId, tecnologiaId, "Titulo persistido", "Contenido persistido");
        when(discussionRepository.save(any(Discussion.class))).thenReturn(guardada);

        // Act
        DiscussionResponse respuesta = discussionService.createDiscussion(autorId, peticionValida());

        // Assert
        assertThat(respuesta.getTitulo()).isEqualTo("Titulo persistido");
        assertThat(respuesta.getContenido()).isEqualTo("Contenido persistido");
    }

    @Test
    @DisplayName("persiste la discusion con estado abierto y fechas de creacion y actualizacion")
    void persisteConEstadoAbiertoYFechas() throws Exception {
        // Arrange
        tecnologiaEncontrada();

        // Act
        discussionService.createDiscussion(autorId, peticionValida());

        // Assert
        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
        assertThat(captor.getValue().getFechaCreacion()).isNotNull();
        assertThat(captor.getValue().getUltimaActualizacion()).isNotNull();
    }

    @Test
    @DisplayName("busca la tecnologia por el identificador recibido en la peticion")
    void buscaLaTecnologiaPorElIdentificadorRecibido() throws Exception {
        // Arrange
        UUID otraTecnologiaId = UUID.randomUUID();
        when(tecnologiaRefRepository.findById(otraTecnologiaId))
                .thenReturn(Optional.of(newTecnologiaRef(otraTecnologiaId, "React")));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invoc -> invoc.getArgument(0));

        // Act
        DiscussionResponse respuesta = discussionService.createDiscussion(
                autorId, peticion("Hooks y estado global", "useContext o Redux", otraTecnologiaId));

        // Assert
        assertThat(respuesta.getTecnologia()).isEqualTo("React");
        verify(tecnologiaRefRepository).findById(otraTecnologiaId);
    }

    // --------------------------------------------------------------- tecnologia

    @Test
    @DisplayName("falla con 400 y TECNOLOGIA_INVALIDA si la tecnologia indicada no existe")
    void fallaSiLaTecnologiaNoExiste() {
        // Arrange
        when(tecnologiaRefRepository.findById(tecnologiaId)).thenReturn(Optional.empty());

        // Act
        BusinessException error = catchThrowableOfType(
                () -> discussionService.createDiscussion(autorId, peticionValida()),
                BusinessException.class);

        // Assert
        assertThat(error).isNotNull();
        assertThat(error.getErrorCode()).isEqualTo("TECNOLOGIA_INVALIDA");
        assertThat(error.getMessage()).isEqualTo("La tecnologia indicada no existe");
        assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("no guarda ninguna discusion si la tecnologia no existe")
    void noGuardaDiscusionSiLaTecnologiaNoExiste() {
        // Arrange
        when(tecnologiaRefRepository.findById(tecnologiaId)).thenReturn(Optional.empty());

        // Act
        catchThrowableOfType(
                () -> discussionService.createDiscussion(autorId, peticionValida()),
                BusinessException.class);

        // Assert
        verify(discussionRepository, never()).save(any());
    }

    private CreateDiscussionRequest peticion(String titulo, String contenido, UUID idTecnologia) {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo(titulo);
        request.setContenido(contenido);
        request.setIdTecnologia(idTecnologia);
        return request;
    }

    private CreateDiscussionRequest peticionValida() {
        return peticion("Spring Security", "Como proteger un endpoint con JWT", tecnologiaId);
    }

    private void tecnologiaEncontrada() throws Exception {
        when(tecnologiaRefRepository.findById(tecnologiaId))
                .thenReturn(Optional.of(newTecnologiaRef(tecnologiaId, "Java")));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invoc -> invoc.getArgument(0));
    }

    // TecnologiaRef no tiene constructor publico (es de solo lectura, a proposito,
    // ver la explicacion en la propia clase). Para esta prueba, la unica forma
    // limpia de construir una instancia sin exponer un constructor publico que
    // nadie mas deberia usar es con reflexion, solo en el test.
    private TecnologiaRef newTecnologiaRef(UUID id, String nombre) throws Exception {
        java.lang.reflect.Constructor<TecnologiaRef> constructor = TecnologiaRef.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        TecnologiaRef tecnologia = constructor.newInstance();
        setField(tecnologia, "id", id);
        setField(tecnologia, "nombreTecnologia", nombre);
        return tecnologia;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = TecnologiaRef.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
