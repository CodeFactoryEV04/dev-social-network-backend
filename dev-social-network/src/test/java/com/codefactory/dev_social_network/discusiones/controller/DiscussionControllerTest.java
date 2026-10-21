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

    private CreateDiscussionRequest peticion(String title, String content, Long technologyId) {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setTechnologyId(technologyId);
        return request;
    }

    private CreateDiscussionRequest peticionValida() {
        return peticion("Spring Security", "Como proteger un endpoint con JWT", 1L);
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
        CreateDiscussionRequest request = peticionValida();
        DiscussionResponse created = new DiscussionResponse();
        created.setId(10L);
        created.setUserId(userId);
        created.setTechnology("Java");
        created.setTitle("Spring Security");
        created.setContent("Como proteger un endpoint con JWT");
        created.setStatus(DiscussionStatus.OPEN);
        when(discussionService.createDiscussion(userId, request)).thenReturn(created);

        // Act
        ResponseEntity<DiscussionResponse> respuesta = controller.createDiscussion(userId, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo(10L);
        assertThat(respuesta.getBody().getUserId()).isEqualTo(userId);
        assertThat(respuesta.getBody().getTechnology()).isEqualTo("Java");
        assertThat(respuesta.getBody().getTitle()).isEqualTo("Spring Security");
        assertThat(respuesta.getBody().getStatus()).isEqualTo(DiscussionStatus.OPEN);
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
        CreateDiscussionRequest request = peticion(null, "Como proteger un endpoint con JWT", 1L);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("title: Title is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("rechaza un titulo vacio o compuesto solo por espacios")
    void rechazaTituloVacio(String title) {
        // Arrange
        CreateDiscussionRequest request = peticion(title, "Como proteger un endpoint con JWT", 1L);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("title: Title is required");
    }

    @Test
    @DisplayName("rechaza un titulo que supera los 150 caracteres")
    void rechazaTituloDemasiadoLargo() {
        // Arrange
        CreateDiscussionRequest request = peticion("a".repeat(151), "Contenido del mensaje", 1L);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("title: Title must not exceed 150 characters");
    }

    @Test
    @DisplayName("acepta un titulo de exactamente 150 caracteres")
    void aceptaTituloDeLongitudMaxima() {
        // Arrange
        CreateDiscussionRequest request = peticion("a".repeat(150), "Contenido del mensaje", 1L);

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
        assertThat(errores).containsExactly("technologyId: Technology is required");
    }

    // ---------------------------------------------------- contenido obligatorio

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("informa que el contenido del mensaje no puede estar vacio")
    void informaQueElContenidoNoPuedeEstarVacio(String content) {
        // Arrange
        CreateDiscussionRequest request = peticion("Spring Security", content, 1L);

        // Act
        Set<String> errores = erroresDeValidacion(request);

        // Assert
        assertThat(errores).containsExactly("content: Content is required");
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
                "title: Title is required",
                "content: Content is required",
                "technologyId: Technology is required");
    }
}
