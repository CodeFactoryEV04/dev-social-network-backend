package com.codefactory.dev_social_network.usuarios.service;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.codefactory.dev_social_network.usuarios.dto.UsuarioDTO;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.UsuarioRepositoryPort;

@ExtendWith(MockitoExtension.class)
class UsuarioQueryServiceImplTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private UsuarioQueryServiceImpl service;

    private Usuario usuarioConId(UUID id, String email) {
        Usuario usuario = new Usuario("Ana", "Ruiz", email);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    @Test
    @DisplayName("mapea la entidad Usuario a un UsuarioDTO con id y email")
    void mapeaEntidadADto() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorEmail("ana@correo.com"))
                .thenReturn(Optional.of(usuarioConId(id, "ana@correo.com")));

        // Act
        Optional<UsuarioDTO> resultado = service.buscarPorEmail("ana@correo.com");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(id);
        assertThat(resultado.get().email()).isEqualTo("ana@correo.com");
    }

    @Test
    @DisplayName("devuelve vacio cuando el email no existe")
    void devuelveVacioSiNoExiste() {
        // Arrange
        when(usuarioRepositoryPort.buscarPorEmail("nadie@correo.com"))
                .thenReturn(Optional.empty());

        // Act
        var resultado = service.buscarPorEmail("nadie@correo.com");

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("delega la busqueda en el puerto de repositorio")
    void delegaEnElPuerto() {
        // Arrange
        when(usuarioRepositoryPort.buscarPorEmail("ana@correo.com"))
                .thenReturn(Optional.empty());

        // Act
        service.buscarPorEmail("ana@correo.com");

        // Assert
        verify(usuarioRepositoryPort).buscarPorEmail("ana@correo.com");
    }
}

