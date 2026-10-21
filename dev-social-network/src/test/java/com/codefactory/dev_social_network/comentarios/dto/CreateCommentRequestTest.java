package com.codefactory.dev_social_network.comentarios.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCommentRequestTest {

    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void deberiaRechazarComentarioSoloConEspacios() {
        Set<ConstraintViolation<CreateCommentRequest>> errores = validar(" ");

        assertThat(errores).hasSize(1);
        assertThat(errores.iterator().next().getMessage())
                .isEqualTo("El comentario no puede estar vacio");
    }

    @Test
    void deberiaRechazarComentarioDe1001Caracteres() {
        Set<ConstraintViolation<CreateCommentRequest>> errores = validar("a".repeat(1001));

        assertThat(errores).hasSize(1);
        assertThat(errores.iterator().next().getMessage())
                .isEqualTo("Comentario excede el limite de caracteres");
    }

    @Test
    void deberiaAceptarComentarioDe1000Caracteres() {
        assertThat(validar("a".repeat(1000))).isEmpty();
    }

    private Set<ConstraintViolation<CreateCommentRequest>> validar(String texto) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setComentario(texto);
        return validator.validate(request);
    }
}
