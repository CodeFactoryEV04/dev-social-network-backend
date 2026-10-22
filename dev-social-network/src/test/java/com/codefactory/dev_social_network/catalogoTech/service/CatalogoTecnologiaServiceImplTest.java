package com.codefactory.dev_social_network.catalogoTech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codefactory.dev_social_network.catalogoTech.entity.TecnologiesEntity;
import com.codefactory.dev_social_network.catalogoTech.entity.TecnologyCategory;
import com.codefactory.dev_social_network.catalogoTech.repository.TecnologiesRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoTecnologiaServiceImplTest {

    @Mock
    private TecnologiesRepository tecnologiasRepository;

    @InjectMocks
    private CatalogoTecnologiaServiceImpl service;

    private TecnologiesEntity tecnologia(String nombre) {
        TecnologiesEntity t = new TecnologiesEntity(nombre, TecnologyCategory.LANGUAGE);
        t.setNameTecnology(nombre);
        return t;
    }

    // ---------------------------------------------- isTecnologyAvailable

    @Test
    @DisplayName("una lista de tecnologias nula se considera disponible")
    void listaNulaEsDisponible() {
        // Act
        var resultado = service.isTecnologyAvailable(null);

        // Assert
        assertThat(resultado).isTrue();
        verify(tecnologiasRepository, never()).existsByNameTecnologyIgnoreCase(anyString());
    }

    @Test
    @DisplayName("una lista vacia se considera disponible")
    void listaVaciaEsDisponible() {
        // Act
        var resultado = service.isTecnologyAvailable(List.of());

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("todas las tecnologias presentes la marcan como disponible")
    void todasLasTecnologiasExisten() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Java")).thenReturn(true);
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Spring")).thenReturn(true);

        // Act
        var resultado = service.isTecnologyAvailable(List.of("Java", "Spring"));

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("una sola tecnologia ausente hace que la lista no sea valida")
    void unaTecnologiaAusenteInvalida() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Java")).thenReturn(true);
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Klingon")).thenReturn(false);

        // Act
        var resultado = service.isTecnologyAvailable(List.of("Java", "Klingon"));

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("recorta el nombre antes de buscar, ignorando espacios sobrantes")
    void normalizaNombreTecnologia() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("jAvA")).thenReturn(true);

        // Act
        var resultado = service.isTecnologyAvailable(List.of("  jAvA  "));

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("un nombre de tecnologia nulo consulta el repositorio con cadena vacia")
    void nombreNuloSeBuscaComoVacio() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("")).thenReturn(false);

        // Act
        var resultado = service.isTecnologyAvailable(java.util.Arrays.asList((String) null));

        // Assert
        assertThat(resultado).isFalse();
    }

    // ---------------------------------------------- obtenerNombresDisponibles

    @Test
    @DisplayName("devuelve los nombres de las tecnologias del catalogo")
    void devuelveNombresDisponibles() {
        // Arrange
        when(tecnologiasRepository.findAll()).thenReturn(List.of(tecnologia("Java"), tecnologia("Spring")));

        // Act
        var resultado = service.obtenerNombresDisponibles();

        // Assert
        assertThat(resultado).containsExactly("Java", "Spring");
    }

    // ------------------------------------------------------- existe

    @Test
    @DisplayName("existe devuelve true cuando la tecnologia esta en el catalogo")
    void existeEnCatalogo() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Java")).thenReturn(true);

        // Act
        var resultado = service.existe("Java");

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("existe devuelve false cuando la tecnologia no esta en el catalogo")
    void noExisteEnCatalogo() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Klingon")).thenReturn(false);

        // Act
        var resultado = service.existe("Klingon");

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("existe devuelve false para un nombre nulo o vacio sin consultar el repositorio")
    void existeConNombreVacio() {
        // Act
        var resultado = service.existe(null);

        // Assert
        assertThat(resultado).isFalse();

        // Act
        var resultado2 = service.existe("   ");

        // Assert
        assertThat(resultado2).isFalse();
        verify(tecnologiasRepository, never()).existsByNameTecnologyIgnoreCase(anyString());
    }

    @Test
    @DisplayName("existe recorta el nombre antes de consultar")
    void existeRecortaElNombre() {
        // Arrange
        when(tecnologiasRepository.existsByNameTecnologyIgnoreCase("Java")).thenReturn(true);

        // Act
        var resultado = service.existe("  Java  ");

        // Assert
        assertThat(resultado).isTrue();
    }
}

