package com.codefactory.dev_social_network.proyectos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codefactory.dev_social_network.proyectos.dto.CreateProjectRequest;
import com.codefactory.dev_social_network.proyectos.dto.ProjectResponse;
import com.codefactory.dev_social_network.proyectos.entity.Project;
import com.codefactory.dev_social_network.proyectos.entity.Technology;
import com.codefactory.dev_social_network.proyectos.repository.ProjectRepository;
import com.codefactory.dev_social_network.proyectos.repository.TechnologyRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private ProjectService service;

    private Technology tecnologia(Long id, String name) {
        Technology t = new Technology();
        t.setId(id);
        t.setName(name);
        t.setType("LENGUAJE");
        return t;
    }

    private CreateProjectRequest request(String repositoryUrl) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Dev Social Network");
        request.setDescription("Red social para desarrolladores");
        request.setRepositoryUrl(repositoryUrl);
        request.setTechnologyIds(List.of(1L));
        return request;
    }

    private void tecnologiasEncontradas() {
        Technology java = tecnologia(1L, "Java");
        when(technologyRepository.findAllById(List.of(1L))).thenReturn(List.of(java));
        when(projectRepository.save(any(Project.class))).thenAnswer(invoc -> {
            Project p = invoc.getArgument(0);
            p.setId(10L);
            return p;
        });
    }

    // ------------------------------------------------------------------ creacion

    @Test
    @DisplayName("crea el proyecto y devuelve la respuesta mapeada")
    void creaElProyecto() {
        // Arrange
        tecnologiasEncontradas();

        // Act
        ProjectResponse respuesta = service.createProject(request("https://github.com/ana/api"));

        // Assert
        assertThat(respuesta.getId()).isEqualTo(10L);
        assertThat(respuesta.getTitle()).isEqualTo("Dev Social Network");
        assertThat(respuesta.getDescription()).isEqualTo("Red social para desarrolladores");
        assertThat(respuesta.getRepositoryUrl()).isEqualTo("https://github.com/ana/api");
    }

    @Test
    @DisplayName("mapea las tecnologias a sus nombres")
    void mapeaNombresDeTecnologias() {
        // Arrange
        tecnologiasEncontradas();

        // Act
        ProjectResponse respuesta = service.createProject(request("https://github.com/ana/api"));

        // Assert
        assertThat(respuesta.getTechnologies()).containsExactly("Java");
    }

    @Test
    @DisplayName("persiste el proyecto con las fechas de creacion y actualizacion")
    void persisteConFechas() {
        // Arrange
        tecnologiasEncontradas();

        // Act
        service.createProject(request("https://github.com/ana/api"));

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
        assertThat(captor.getValue().getUpdatedAt()).isNotNull();
    }

    // ------------------------------------------------------------- tecnologias

    @Test
    @DisplayName("falla si alguna tecnologia solicitada no existe")
    void fallaSiFaltaAlgunaTecnologia() {
        // Arrange
        when(technologyRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(tecnologia(1L, "Java")));
        CreateProjectRequest request = request("https://github.com/ana/api");
        request.setTechnologyIds(List.of(1L, 2L));

        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("technologies are invalid");

        // Assert
        verify(projectRepository, never()).save(any());
    }

    // ------------------------------------------------------------------- URLs

    @ParameterizedTest
    @ValueSource(strings = {
            "https://github.com/ana/api",
            "https://GitHub.com/ana/api",
            "https://www.github.com/ana/api",
            "https://gitlab.com/ana/api",
            "https://bitbucket.org/ana/api",
            "https://sub.gitlab.com/ana/api"
    })
    @DisplayName("acepta URLs de GitHub, GitLab y Bitbucket")
    void aceptaDominiosPermitidos(String url) {
        // Arrange
        tecnologiasEncontradas();

        // Act y Assert
        assertThatCode(() -> service.createProject(request(url))).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("una URL de repositorio vacia es opcional")
    void urlVaciaEsOpcional(String url) {
        // Arrange
        tecnologiasEncontradas();

        // Act y Assert
        assertThatCode(() -> service.createProject(request(url))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("rechaza una URL que no sea https")
    void rechazaUrlSinHttps() {
        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request("http://github.com/ana/api")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("valid HTTPS URL");
    }

    @Test
    @DisplayName("rechaza un dominio que no sea GitHub, GitLab ni Bitbucket")
    void rechazaDominioNoPermitido() {
        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request("https://mi-empresa.com/api")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("GitHub, GitLab or Bitbucket");
    }

    @Test
    @DisplayName("rechaza una URL malformada")
    void rechazaUrlMalformada() {
        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request("https://mi empresa.com/api")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid repository URL");
    }

    @Test
    @DisplayName("una URL sin esquema se rechaza como HTTPS invalido")
    void rechazaUrlSinEsquema() {
        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request("github.com/ana/api")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("valid HTTPS URL");
    }

    @Test
    @DisplayName("no busca tecnologias si la URL ya es invalida")
    void validaUrlAntesDeBuscarTecnologias() {
        // Act y Assert
        assertThatThrownBy(() -> service.createProject(request("https://mi-empresa.com/api")))
                .isInstanceOf(BusinessException.class);

        // Assert
        verify(technologyRepository, never()).findAllById(any());
    }
}

