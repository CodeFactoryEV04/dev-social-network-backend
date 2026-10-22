package com.codefactory.dev_social_network.usuarios.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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

import com.codefactory.dev_social_network.usuarios.entity.UserProfileEntity;

@ExtendWith(MockitoExtension.class)
class UserProfileRepositoryAdapterTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileRepositoryAdapter adapter;

    @Test
    @DisplayName("buscarPorUsuarioId devuelve el perfil asociado al usuario")
    void buscaPorUsuarioId() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UserProfileEntity perfil = mock(UserProfileEntity.class);
        when(userProfileRepository.findByUserId_Id(usuarioId)).thenReturn(Optional.of(perfil));

        // Act
        Optional<UserProfileEntity> resultado = adapter.buscarPorUsuarioId(usuarioId);

        // Assert
        assertThat(resultado).contains(perfil);
        verify(userProfileRepository).findByUserId_Id(usuarioId);
    }

    @Test
    @DisplayName("buscarPorUsuarioId devuelve vacio si el usuario no tiene perfil")
    void buscaPorUsuarioIdNoEncontrado() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        when(userProfileRepository.findByUserId_Id(usuarioId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfileEntity> resultado = adapter.buscarPorUsuarioId(usuarioId);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("guardar delega en el repositorio JPA")
    void guarda() {
        // Arrange
        UserProfileEntity perfil = mock(UserProfileEntity.class);
        when(userProfileRepository.save(perfil)).thenReturn(perfil);

        // Act
        UserProfileEntity resultado = adapter.guardar(perfil);

        // Assert
        assertThat(resultado).isSameAs(perfil);
        verify(userProfileRepository).save(perfil);
    }
}
