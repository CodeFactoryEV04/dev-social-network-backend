package com.codefactory.dev_social_network.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("un error de validacion responde 400 con el detalle de cada campo")
    void validacionResponde400ConDetalles() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("usuario", "email", "El email es obligatorio"),
                new FieldError("usuario", "password", "La contraseña es obligatoria")));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<ApiErrorResponse> respuesta = handler.handleValidationException(exception, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiErrorResponse body = respuesta.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getMessage()).isEqualTo("Request validation failed");
        assertThat(body.getTraceId()).isNotBlank();
        assertThat(body.getDetails())
                .extracting(ErrorDetail::getField, ErrorDetail::getMessage)
                .containsExactly(
                        tuple("email", "El email es obligatorio"),
                        tuple("password", "La contraseña es obligatoria"));
    }

    @Test
    @DisplayName("una excepcion de negocio responde con su codigo, mensaje y estado")
    void negocioRespondeConSuCodigoYEstado() {
        // Arrange
        BusinessException exception =
                new BusinessException("EMAIL_DUPLICADO", "El email ya esta registrado", HttpStatus.CONFLICT);

        // Act
        ResponseEntity<ApiErrorResponse> respuesta = handler.handleBusinessException(exception, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiErrorResponse body = respuesta.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo("EMAIL_DUPLICADO");
        assertThat(body.getMessage()).isEqualTo("El email ya esta registrado");
        assertThat(body.getDetails()).isEmpty();
        assertThat(body.getTraceId()).isNotBlank();
    }

    @Test
    @DisplayName("un recurso inexistente responde 404 indicando la ruta")
    void recursoInexistenteResponde404() {
        // Arrange
        NoResourceFoundException exception = mock(NoResourceFoundException.class);
        when(exception.getResourcePath()).thenReturn("api/v1/no-existe");

        // Act
        ResponseEntity<ApiErrorResponse> respuesta = handler.handleNoResourceFound(exception, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiErrorResponse body = respuesta.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(body.getMessage()).isEqualTo("El recurso solicitado no existe: api/v1/no-existe");
        assertThat(body.getDetails()).isEmpty();
        assertThat(body.getTraceId()).isNotBlank();
    }

    @Test
    @DisplayName("una excepcion no controlada responde 500 sin exponer el detalle")
    void excepcionGenericaResponde500() {
        // Arrange
        Exception exception = new IllegalStateException("detalle interno");

        // Act
        ResponseEntity<ApiErrorResponse> respuesta = handler.handleGenericException(exception, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiErrorResponse body = respuesta.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(body.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(body.getDetails()).isEmpty();
        assertThat(body.getTraceId()).isNotBlank();
    }

    @Test
    @DisplayName("cada respuesta lleva un traceId distinto")
    void cadaRespuestaTieneTraceIdDistinto() {
        // Arrange
        Exception exception = new RuntimeException();

        // Act
        String primero = handler.handleGenericException(exception, request).getBody().getTraceId();
        String segundo = handler.handleGenericException(exception, request).getBody().getTraceId();

        // Assert
        assertThat(primero).isNotEqualTo(segundo);
    }
}
