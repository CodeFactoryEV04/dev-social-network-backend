package com.codefactory.dev_social_network.comentarios.entity;
 
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ComentarioProyectoTest {

    @Test
    void deberiaAlmacenarLosDatosCorrectamenteAlCrear() {
        UUID idProyecto = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();

        ComentarioProyecto comentario = new ComentarioProyecto(idProyecto, idUsuario, "Muy buen proyecto");

        assertThat(comentario.getIdProyecto()).isEqualTo(idProyecto);
        assertThat(comentario.getIdUsuario()).isEqualTo(idUsuario);
        assertThat(comentario.getComentario()).isEqualTo("Muy buen proyecto");
    }

    @Test
    void deberiaAsignarFechaDeCreacionAutomaticamente() {
        OffsetDateTime antes = OffsetDateTime.now();

        ComentarioProyecto comentario = new ComentarioProyecto(
                UUID.randomUUID(), UUID.randomUUID(), "Comentario de prueba"
        );

        OffsetDateTime despues = OffsetDateTime.now();

        assertThat(comentario.getFechaCreacion())
                .isAfterOrEqualTo(antes)
                .isBeforeOrEqualTo(despues);
    }
}