package com.codefactory.dev_social_network.usuarios.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioRequest;
import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioResponse;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @InjectMocks
    private UsuarioController controller;

    // ------------------------------------------------------------------ POST

    @Test
    @DisplayName("registra un usuario y responde 201 con el id generado")
    void registraUsuario() {
        // Arrange
        UUID nuevoId = UUID.randomUUID();
        RegistroUsuarioRequest request =
                new RegistroUsuarioRequest("ana.ruiz", "ana@correo.com", "Ana", "Ruiz", "Secreto1!");
        when(registrarUsuarioUseCase.registrar(any())).thenReturn(nuevoId);

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
                new RegistroUsuarioRequest("ana.ruiz", "ana@correo.com", "Ana", "Ruiz", "Secreto1!");
        when(registrarUsuarioUseCase.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        controller.registrar(request);

        // Assert
        verify(registrarUsuarioUseCase).registrar(
                new RegistrationData("ana.ruiz", "ana@correo.com", "Ana", "Ruiz", "Secreto1!"));
    }
}
