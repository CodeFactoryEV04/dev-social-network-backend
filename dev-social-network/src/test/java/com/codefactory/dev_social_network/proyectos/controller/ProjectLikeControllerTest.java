package com.codefactory.dev_social_network.proyectos.controller;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikeService;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU010 · ProjectLikeController")
class ProjectLikeControllerTest {

    private static final UUID LUISDEV_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private ProjectLikeService projectLikeService;

    @InjectMocks
    private ProjectLikeController controller;

    @Test
    @DisplayName("C01 · POST like responde 201 con el body del service")
    void likeProject_ok_responde201() {
        ProjectLikeResponse esperado = new ProjectLikeResponse(1024L, "DevConnect", 1L, true);
        when(projectLikeService.likeProject(1024L, LUISDEV_ID)).thenReturn(esperado);

        ResponseEntity<ProjectLikeResponse> response = controller.likeProject(1024L, LUISDEV_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(esperado);
        verify(projectLikeService).likeProject(1024L, LUISDEV_ID); // el usuario autenticado llega al service
    }

    @Test
    @DisplayName("C02 · GET mis likes responde 200 con la lista")
    void getMyLikedProjects_ok_responde200() {
        List<LikedProjectResponse> lista = List.of(new LikedProjectResponse(
                1024L, "DevConnect", "https://github.com/luisdev/devconnect",
                OffsetDateTime.parse("2026-10-12T10:00:00-05:00")));
        when(projectLikeService.getMyLikedProjects(LUISDEV_ID)).thenReturn(lista);

        ResponseEntity<List<LikedProjectResponse>> response = controller.getMyLikedProjects(LUISDEV_ID);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(lista);
    }

    @Test
    @DisplayName("C03 · Si el service lanza BusinessException, se propaga")
    void likeProject_serviceLanzaExcepcion_sePropaga() {
        when(projectLikeService.likeProject(9999L, LUISDEV_ID))
                .thenThrow(new BusinessException("PROJECT_NOT_FOUND", "El proyecto no existe", HttpStatus.NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.likeProject(9999L, LUISDEV_ID));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}