package com.codefactory.dev_social_network.autenticacion.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.codefactory.dev_social_network.autenticacion.dto.LoginRequestDTO;
import com.codefactory.dev_social_network.autenticacion.dto.LoginResponseDTO;
import com.codefactory.dev_social_network.autenticacion.interfaces.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    @DisplayName("inicia sesion y responde 200 con el token generado")
    void iniciaSesion() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("ana@correo.com", "Secreto1!");
        LoginResponseDTO login = new LoginResponseDTO("ana@correo.com", "Bienvenido", "jwt.token");
        when(authService.iniciarSesion(request)).thenReturn(login);

        // Act
        ResponseEntity<LoginResponseDTO> respuesta = controller.login(request);

        // Assert
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getToken()).isEqualTo("jwt.token");
        assertThat(respuesta.getBody().getEmail()).isEqualTo("ana@correo.com");
        assertThat(respuesta.getBody().getMensaje()).isEqualTo("Bienvenido");
    }

    @Test
    @DisplayName("delega el login en el servicio de autenticacion")
    void delegaEnElServicio() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("ana@correo.com", "Secreto1!");
        when(authService.iniciarSesion(request)).thenReturn(new LoginResponseDTO(null, null, null));

        // Act
        controller.login(request);

        // Assert
        verify(authService).iniciarSesion(request);
    }
}
