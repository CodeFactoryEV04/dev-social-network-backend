package com.codefactory.dev_social_network.discusiones.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.service.DiscussionService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class DiscussionControllerTest {

    private static final UUID ID_TECNOLOGIA = UUID.randomUUID();

    @Mock
    private DiscussionService discussionService;

    @InjectMocks
    private DiscussionController controller;

    private static jakarta.validation.ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    private CreateDiscussionRequest peticion(String titulo, String contenido, UUID idTecnologia) {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo(titulo);
        request.setContenido(contenido);
        request.setIdTecnologia(idTecnologia);
        return request;
    }

    private CreateDiscussionRequest peticionValida() {
        return peticion("Spring Security", "Como proteger un endpoint con JWT", ID_TECNOLOGIA);
    }

    private Set<String> erroresDeValidacion(CreateDiscussionRequest request) {
        return validator.validate(request).stream()
                .map(DiscussionControllerTest::describir)
                .collect(Collectors.toSet());
    }

    private static String describir(ConstraintViolation<CreateDiscussionRequest> violacion) {
        return violacion.getPropertyPath() + ": " + violacion.getMessage();
    }

    // -------------------------------------------------------- creacion exitosa

    @Test
    @DisplayName("crea la discusion y responde 201 con la discusion creada")
    void creaLaDiscusion() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID discussionId = UUID.randomUUID();
        CreateDiscussionRequest request = peticionValida();
        DiscussionResponse created = new DiscussionResponse();
        created.setId(discussionId);
        created.setAutorId(userId);
        created.setTecnologia("Java");
        created.setTitulo("Spring Security");
        created.setContenido("Como proteger un endpoint con JWT");
        created.setEstado(DiscussionStatus.ABIERTA);
        when(discussionService.createDiscussion(userId, request)).thenReturn(created);

        // Act
        ResponseEntity<DiscussionResponse> respuesta = controller.createDiscussion(userId, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo(discussionId);
        assertThat(respuesta.getBody().getAutorId()).isEqualTo(userId);
        assertThat(respuesta.getBody().getTecnologia()).isEqualTo("Java");
        assertThat(respuesta.getBody().getTitulo()).isEqualTo("Spring Security");
        assertThat(respuesta.getBody().getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
    }

    @Test
    @DisplayName("delega la creacion en el servicio con el usuario autenticado y el request recibido")
    void delegaLaCreacionEnElServicio() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateDiscussionRequest request = peticionValida();
        when(discussionService.createDiscussion(userId, request)).thenReturn(new DiscussionResponse());

        // Act
        controller.createDiscussion(userId, request);

        // Assert
        verify(discussionService).createDiscussion(eq(userId), eq(request));
    }

    @Test
    @DisplayName("el request del servicio no tiene errores de validacion")
    void elRequestValidoNoTieneErroresDeValidacion() {
        // Arrange
        CreateDiscussionRequest request = peticionValida();

        // Act y Assert
        assertThat(erroresDeValidacion(request)).isEmpty();
    }

    // ------------------------------------------------------ titulo obligatorio

    @Test
    @DisplayName("informa que el titulo es obligatorio cuando no se proporciona")
    void informaQueElTituloEsObligatorio() {
        // Arrange
        CreateDiscussionRequest request = peticion(null, "Como proteger un endpoint con JWT", ID_TECNOLOGIA);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("titulo: El titulo es obligatorio");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("rechaza un titulo vacio o compuesto solo por espacios")
    void rechazaTituloVacio(String titulo) {
        // Arrange
        CreateDiscussionRequest request = peticion(titulo, "Como proteger un endpoint con JWT", ID_TECNOLOGIA);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("titulo: El titulo es obligatorio");
    }

    @Test
    @DisplayName("rechaza un titulo que supera los 150 caracteres")
    void rechazaTituloDemasiadoLargo() {
        // Arrange
        CreateDiscussionRequest request = peticion("a".repeat(151), "Contenido del mensaje", ID_TECNOLOGIA);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("titulo: El titulo no puede superar los 150 caracteres");
    }

    @Test
    @DisplayName("acepta un titulo de exactamente 150 caracteres")
    void aceptaTituloDeLongitudMaxima() {
        // Arrange
        CreateDiscussionRequest request = peticion("a".repeat(150), "Contenido del mensaje", ID_TECNOLOGIA);

        // Act y Assert
        assertThat(erroresDeValidacion(request)).isEmpty();
    }

    // ---------------------------------------------------- tecnologia obligatoria

    @Test
    @DisplayName("informa que debe especificar al menos una tecnologia cuando no se indica")
    void informaQueLaTecnologiaEsObligatoria() {
        // Arrange
        CreateDiscussionRequest request = peticion("Spring Security", "Contenido del mensaje", null);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("idTecnologia: La tecnologia es obligatoria");
    }

    // ---------------------------------------------------- contenido obligatorio

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("informa que el contenido del mensaje no puede estar vacio")
    void informaQueElContenidoNoPuedeEstarVacio(String contenido) {
        // Arrange
        CreateDiscussionRequest request = peticion("Spring Security", contenido, ID_TECNOLOGIA);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("contenido: El contenido es obligatorio");
    }

    @Test
    @DisplayName("informa a la vez de los tres campos obligatorios ausentes")
    void informaDeTodosLosCamposObligatoriosAusentes() {
        // Arrange
        CreateDiscussionRequest request = peticion(null, "", null);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactlyInAnyOrder(
                "titulo: El titulo es obligatorio",
                "contenido: El contenido es obligatorio",
                "idTecnologia: La tecnologia es obligatoria");
    }
}
