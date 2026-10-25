package com.codefactory.dev_social_network.proyectos.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HU010 · ProjectLike (entidad)")
class ProjectLikeTest {

    @Test
    @DisplayName("E01 · El constructor asigna proyecto, usuario y fecha de creación")
    void constructor_asignaCamposYFecha() {
        Project devconnect = new Project();
        UUID luisDev = UUID.fromString("11111111-1111-1111-1111-111111111111");

        OffsetDateTime antes = OffsetDateTime.now();
        ProjectLike like = new ProjectLike(devconnect, luisDev);
        OffsetDateTime despues = OffsetDateTime.now();

        assertThat(like.getProject()).isSameAs(devconnect);
        assertThat(like.getUsuarioId()).isEqualTo(luisDev);
        assertThat(like.getCreatedAt()).isBetween(antes, despues);
    }
}