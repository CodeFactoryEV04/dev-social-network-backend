package com.codefactory.dev_social_network.proyectos.repository;

import com.codefactory.dev_social_network.proyectos.dto.ProjectSummary;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU010 · ProjectFinderAdapter")
class ProjectFinderAdapterTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectFinderAdapter adapter;

    @Test
    @DisplayName("F01 · Proyecto existente → resumen con id y título")
    void buscarResumenPorId_existe_retornaResumen() {
        Project devconnect = new Project();
        devconnect.setId(1024L);
        devconnect.setTitle("DevConnect");
        when(projectRepository.findById(1024L)).thenReturn(Optional.of(devconnect));

        Optional<ProjectSummary> resultado = adapter.buscarResumenPorId(1024L);

        assertThat(resultado).contains(new ProjectSummary(1024L, "DevConnect"));
    }

    @Test
    @DisplayName("F02 · Proyecto inexistente → Optional vacío")
    void buscarResumenPorId_noExiste_retornaVacio() {
        when(projectRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThat(adapter.buscarResumenPorId(9999L)).isEmpty();
    }
}