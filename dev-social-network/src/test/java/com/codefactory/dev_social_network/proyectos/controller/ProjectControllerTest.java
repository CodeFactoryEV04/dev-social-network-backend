package com.codefactory.dev_social_network.proyectos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.codefactory.dev_social_network.proyectos.dto.CreateProjectRequest;
import com.codefactory.dev_social_network.proyectos.dto.ProjectResponse;
import com.codefactory.dev_social_network.proyectos.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController controller;

    @Test
    @DisplayName("crea el proyecto y responde 201 con el proyecto creado")
    void creaProyecto() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Dev Social Network");
        request.setDescription("Red social para desarrolladores");
        request.setRepositoryUrl("https://github.com/ana/api");
        request.setTechnologyIds(List.of(1L));

        ProjectResponse created = new ProjectResponse();
        created.setId(10L);
        created.setTitle("Dev Social Network");
        created.setTechnologies(List.of("Java"));

        when(projectService.createProject(any())).thenReturn(created);

        // Act
        ResponseEntity<ProjectResponse> respuesta = controller.createProject(request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo(10L);
        assertThat(respuesta.getBody().getTechnologies()).containsExactly("Java");
    }

    @Test
    @DisplayName("delega la creacion en el servicio con el request recibido")
    void delegaEnElServicio() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Dev Social Network");
        request.setDescription("Red social para desarrolladores");
        request.setTechnologyIds(List.of(1L));
        when(projectService.createProject(any())).thenReturn(new ProjectResponse());

        // Act
        controller.createProject(request);

        // Assert
        verify(projectService).createProject(request);
    }
}
