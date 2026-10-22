package com.codefactory.dev_social_network.catalogoTech.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codefactory.dev_social_network.catalogoTech.entity.TecnologiesEntity;
import com.codefactory.dev_social_network.catalogoTech.entity.TecnologyCategory;
import com.codefactory.dev_social_network.catalogoTech.repository.TecnologiesRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoTecnologiaDataInitializerTest {

    @Mock
    private TecnologiesRepository repository;

    @Test
    @DisplayName("carga el catalogo de tecnologias cuando la tabla esta vacia")
    void cargaCatalogoVacio() throws Exception {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());
        CatalogoTecnologiaDataInitializer initializer = new CatalogoTecnologiaDataInitializer();

        // Act
        initializer.cargarCatalogoTecnologia(repository).run();

        // Assert
        ArgumentCaptor<TecnologiesEntity> captor = ArgumentCaptor.forClass(TecnologiesEntity.class);
        verify(repository, times(30)).save(captor.capture());

        List<TecnologiesEntity> guardadas = captor.getAllValues();
        assertThat(guardadas).hasSize(30);
        assertThat(guardadas).extracting(TecnologiesEntity::getNameTecnology)
                .contains("Java", "Python", "Spring Boot", "React", "Backend Developer");
    }

    @Test
    @DisplayName("el seed cubre los tres tipos de categoria")
    void cubreLasTresCategorias() throws Exception {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());
        CatalogoTecnologiaDataInitializer initializer = new CatalogoTecnologiaDataInitializer();

        // Act
        initializer.cargarCatalogoTecnologia(repository).run();

        // Assert
        ArgumentCaptor<TecnologiesEntity> captor = ArgumentCaptor.forClass(TecnologiesEntity.class);
        verify(repository, times(30)).save(captor.capture());

        assertThat(captor.getAllValues()).extracting(TecnologiesEntity::getCategory)
                .contains(TecnologyCategory.ROLE,
                        TecnologyCategory.LANGUAGE,
                        TecnologyCategory.FRAMEWORK);
    }

    @Test
    @DisplayName("no inserta nada si el catalogo ya tiene datos")
    void noInsertaSiYaHayDatos() throws Exception {
        // Arrange
        when(repository.findAll())
                .thenReturn(List.of(new TecnologiesEntity("Java", TecnologyCategory.LANGUAGE)));
        CatalogoTecnologiaDataInitializer initializer = new CatalogoTecnologiaDataInitializer();

        // Act
        initializer.cargarCatalogoTecnologia(repository).run();

        // Assert
        verify(repository, never()).save(any());
    }
}
