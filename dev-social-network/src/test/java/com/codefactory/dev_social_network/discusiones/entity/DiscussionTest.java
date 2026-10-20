package com.codefactory.dev_social_network.discusiones.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiscussionTest {

    @Test
    void deberiaIniciarEnEstadoAbiertaAlCrear() {
        Discussion discussion = new Discussion(
                UUID.randomUUID(), UUID.randomUUID(), "Titulo", "Contenido de prueba"
        );

        assertThat(discussion.getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
    }

    @Test
    void deberiaTenerFechaCreacionIgualAUltimaActualizacionAlCrear() {
        Discussion discussion = new Discussion(
                UUID.randomUUID(), UUID.randomUUID(), "Titulo", "Contenido de prueba"
        );

        assertThat(discussion.getFechaCreacion()).isEqualTo(discussion.getUltimaActualizacion());
    }

    @Test
    void deberiaAlmacenarLosDatosCorrectamenteAlCrear() {
        UUID autorId = UUID.randomUUID();
        UUID idTecnologia = UUID.randomUUID();

        Discussion discussion = new Discussion(autorId, idTecnologia, "Como usar JPA", "Tengo dudas sobre @Entity");

        assertThat(discussion.getAutorId()).isEqualTo(autorId);
        assertThat(discussion.getIdTecnologia()).isEqualTo(idTecnologia);
        assertThat(discussion.getTitulo()).isEqualTo("Como usar JPA");
        assertThat(discussion.getContenido()).isEqualTo("Tengo dudas sobre @Entity");
    }
}
