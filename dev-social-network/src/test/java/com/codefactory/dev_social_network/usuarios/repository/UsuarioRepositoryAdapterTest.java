package com.codefactory.dev_social_network.usuarios.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codefactory.dev_social_network.usuarios.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioJpaRepository jpaRepository;

    @InjectMocks
    private UsuarioRepositoryAdapter adapter;

    @Test
    @DisplayName("existePorEmail delega en el repositorio JPA")
    void existePorEmail() {
        // Arrange
        when(jpaRepository.existsByEmail("ana@correo.com")).thenReturn(true);

        // Act
        var resultado = adapter.existePorEmail("ana@correo.com");

        // Assert
        assertThat(resultado).isTrue();
        verify(jpaRepository).existsByEmail("ana@correo.com");
    }

    @Test
    @DisplayName("existePorEmail devuelve false cuando el email no esta registrado")
    void existePorEmailDevuelveFalse() {
        // Arrange
        when(jpaRepository.existsByEmail("nuevo@correo.com")).thenReturn(false);

        // Act
        var resultado = adapter.existePorEmail("nuevo@correo.com");

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("guardar delega en el repositorio JPA")
    void guardar() {
        // Arrange
        Usuario usuario = new Usuario("Ana", "Ruiz", "ana@correo.com");
        when(jpaRepository.save(usuario)).thenReturn(usuario);

        // Act
        var resultado = adapter.guardar(usuario);

        // Assert
        assertThat(resultado).isSameAs(usuario);
        verify(jpaRepository).save(usuario);
    }

    @Test
    @DisplayName("buscarPorEmail devuelve el usuario encontrado")
    void buscarPorEmail() {
        // Arrange
        Usuario usuario = new Usuario("Ana", "Ruiz", "ana@correo.com");
        when(jpaRepository.findByEmail("ana@correo.com")).thenReturn(Optional.of(usuario));

        // Act
        var resultado = adapter.buscarPorEmail("ana@correo.com");

        // Assert
        assertThat(resultado).contains(usuario);
    }

    @Test
    @DisplayName("buscarPorEmail devuelve vacio si no existe")
    void buscarPorEmailNoEncontrado() {
        // Arrange
        when(jpaRepository.findByEmail("nadie@correo.com")).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.buscarPorEmail("nadie@correo.com");

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId devuelve el usuario encontrado")
    void buscarPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario("Ana", "Ruiz", "ana@correo.com");
        when(jpaRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        var resultado = adapter.buscarPorId(id);

        // Assert
        assertThat(resultado).contains(usuario);
    }

    @Test
    @DisplayName("buscarPorId devuelve vacio si no existe")
    void buscarPorIdNoEncontrado() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.buscarPorId(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}

