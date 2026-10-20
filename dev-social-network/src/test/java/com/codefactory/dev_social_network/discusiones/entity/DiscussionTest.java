package com.codefactory.dev_social_network.discusiones.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiscussionTest {

    @Test
    void shouldStartAsAbiertaWhenCreated() {

        Discussion discussion = new Discussion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Como configurar Spring Security con JWT?",
                "Me sale un 403 incluso con un token valido."
        );

        assertThat(discussion.getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
    }

    @Test
    void shouldSetFechaCreacionAndUltimaActualizacionToTheSameInstantWhenCreated() {

        Discussion discussion = new Discussion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Como configurar Spring Security con JWT?",
                "Me sale un 403 incluso con un token valido."
        );

        assertThat(discussion.getFechaCreacion()).isEqualTo(discussion.getUltimaActualizacion());
    }

    @Test
    void shouldStoreAllProvidedFieldsWhenCreated() {

        UUID autorId = UUID.randomUUID();
        UUID idTecnologia = UUID.randomUUID();
        String titulo = "Como configurar Spring Security con JWT?";
        String contenido = "Me sale un 403 incluso con un token valido.";

        Discussion discussion = new Discussion(autorId, idTecnologia, titulo, contenido);

        assertThat(discussion.getAutorId()).isEqualTo(autorId);
        assertThat(discussion.getIdTecnologia()).isEqualTo(idTecnologia);
        assertThat(discussion.getTitulo()).isEqualTo(titulo);
        assertThat(discussion.getContenido()).isEqualTo(contenido);
    }
}
