package com.codefactory.dev_social_network.usuarios.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioRequest;
import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioResponse;
import com.codefactory.dev_social_network.usuarios.dto.UserProfileEditionRequestDTO;
import com.codefactory.dev_social_network.usuarios.dto.UserProfileResponseDTO;
import com.codefactory.dev_social_network.usuarios.entity.ExperienceLevel;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.UserProfileService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UsuarioController controller;

    private UserProfileResponseDTO perfilResponse(UUID userId) {
        return new UserProfileResponseDTO(
                userId, "Ana", "Ruiz", "ana@correo.com",
                List.of(), List.of("Java"), 3, ExperienceLevel.JUNIOR);
    }

    // ------------------------------------------------------------------ POST

    @Test
    @DisplayName("registra un usuario y responde 201 con el id generado")
    void registraUsuario() {
        // Arrange
        UUID nuevoId = UUID.randomUUID();
        RegistroUsuarioRequest request =
                new RegistroUsuarioRequest("ana@correo.com", "Ana", "Ruiz", "Secreto1!");
        when(registrarUsuarioUseCase.registrar(any(), any(), any(), any())).thenReturn(nuevoId);

        // Act
        ResponseEntity<RegistroUsuarioResponse> respuesta = controller.registrar(request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().id()).isEqualTo(nuevoId);
        assertThat(respuesta.getBody().mensaje()).isEqualTo("Cuenta creada exitosamente");
    }

    @Test
    @DisplayName("descompone el request en los parametros del caso de uso")
    void delegaLosCamposDelRequest() {
        // Arrange
        RegistroUsuarioRequest request =
                new RegistroUsuarioRequest("ana@correo.com", "Ana", "Ruiz", "Secreto1!");
        when(registrarUsuarioUseCase.registrar(any(), any(), any(), any()))
                .thenReturn(UUID.randomUUID());

        // Act
        controller.registrar(request);

        // Assert
        verify(registrarUsuarioUseCase).registrar(
                "ana@correo.com", "Ana", "Ruiz", "Secreto1!");
    }

    // ------------------------------------------------------------------- PUT

    @Test
    @DisplayName("actualiza el perfil y responde 200 con el perfil devuelto")
    void actualizaPerfil() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserProfileEditionRequestDTO request = new UserProfileEditionRequestDTO();
        request.setGithubLink("http://github.com/ana-ruiz");
        request.setYearsOfExperience(3);
        when(userProfileService.updateUserProfile(eq(userId), any()))
                .thenReturn(perfilResponse(userId));

        // Act
        ResponseEntity<UserProfileResponseDTO> respuesta =
                controller.actualizarPerfil(userId, request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().nombre()).isEqualTo("Ana");
        verify(userProfileService).updateUserProfile(userId, request);
    }

    // ------------------------------------------------------------------- GET

    @Test
    @DisplayName("obtiene el perfil y responde 200")
    void obtienePerfil() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userProfileService.getUserProfile(userId)).thenReturn(perfilResponse(userId));

        // Act
        ResponseEntity<UserProfileResponseDTO> respuesta = controller.obtenerPerfil(userId);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().userId()).isEqualTo(userId);
        assertThat(respuesta.getBody().skills()).containsExactly("Java");
    }
}
