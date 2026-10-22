package com.codefactory.dev_social_network.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codefactory.dev_social_network.autenticacion.interfaces.CredencialService;
import com.codefactory.dev_social_network.shared.exception.EmailDuplicadoException;
import com.codefactory.dev_social_network.shared.exception.FormatoEmailInvalidoException;
import com.codefactory.dev_social_network.shared.exception.PasswordInseguraException;
import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.EmailExistencePort;
import com.codefactory.dev_social_network.usuarios.interfaces.PasswordHasher;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;
import com.codefactory.dev_social_network.usuarios.interfaces.UserRegistrationPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UsernameExistencePort;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseImplTest {

    private static final String USERNAME = "ana.ruiz";
    private static final String EMAIL = "ana@correo.com";
    private static final String PASSWORD = "Secreto1!";

    @Mock
    private UserRegistrationPort userRegistrationPort;
    @Mock
    private CredencialService credencialService;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private EmailExistencePort emailExistencePort;
    @Mock
    private UsernameExistencePort usernameExistencePort;

    private RegistrarUsuarioUseCaseImpl service;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        // Mismas reglas y mismo orden (@Order) con el que Spring las inyecta
        List<RegistrationRule> reglas = List.of(
                new UsernameFormatRule(new UsernameValidator()),
                new EmailFormatRule(new EmailValidator()),
                new PasswordStrengthRule(new PasswordValidator()),
                new EmailUniqueRule(emailExistencePort),
                new UsernameUniqueRule(usernameExistencePort));
        service = new RegistrarUsuarioUseCaseImpl(userRegistrationPort, credencialService,
                passwordHasher, reglas);
        usuarioId = UUID.randomUUID();
    }

    private RegistrationData datos(String email, String contrasena) {
        return new RegistrationData(USERNAME, email, "Ana", "Ruiz", contrasena);
    }

    @Test
    @DisplayName("registra el usuario y su credencial, y devuelve el id")
    void registraUsuarioYCredencial() {
        // Arrange
        when(emailExistencePort.existePorEmail(EMAIL)).thenReturn(false);
        when(usernameExistencePort.existePorUsername(USERNAME)).thenReturn(false);
        when(userRegistrationPort.registrar(USERNAME, "Ana", "Ruiz", EMAIL)).thenReturn(usuarioId);
        when(passwordHasher.hashear(PASSWORD)).thenReturn("hash-bcrypt");

        // Act
        UUID resultado = service.registrar(datos(EMAIL, PASSWORD));

        // Assert
        assertThat(resultado).isEqualTo(usuarioId);
        verify(passwordHasher).hashear(PASSWORD);
        verify(credencialService).crearCredencialLocal(usuarioId, "hash-bcrypt");
    }

    @Test
    @DisplayName("guarda el usuario con nombre, apellido y email")
    void guardaUsuarioConTodosLosDatos() {
        // Arrange
        when(userRegistrationPort.registrar(USERNAME, "Ana", "Ruiz", EMAIL)).thenReturn(usuarioId);
        when(passwordHasher.hashear(PASSWORD)).thenReturn("hash-bcrypt");

        // Act
        service.registrar(datos(EMAIL, PASSWORD));

        // Assert
        verify(userRegistrationPort).registrar(USERNAME, "Ana", "Ruiz", EMAIL);
    }

    @Test
    @DisplayName("rechaza un email con formato invalido")
    void rechazaEmailInvalido() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos("no-es-email", PASSWORD)))
                .isInstanceOf(FormatoEmailInvalidoException.class);

        // Assert
        verify(userRegistrationPort, never()).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("rechaza un email ya registrado")
    void rechazaEmailDuplicado() {
        // Arrange
        when(emailExistencePort.existePorEmail(EMAIL)).thenReturn(true);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, PASSWORD)))
                .isInstanceOf(EmailDuplicadoException.class);

        // Assert
        verify(userRegistrationPort, never()).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("rechaza una contrasena corta")
    void rechazaContrasenaCorta() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, "Aa1!")))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("8 caracteres");

        // Assert
        verify(userRegistrationPort, never()).registrar(any(), any(), any(), any());
    }

    @Test
    @DisplayName("rechaza una contrasena sin mayuscula")
    void rechazaContrasenaSinMayuscula() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, "secreto1!")))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("mayúscula");
    }

    @Test
    @DisplayName("rechaza una contrasena sin numero")
    void rechazaContrasenaSinNumero() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, "Secreto!")))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("número");
    }

    @Test
    @DisplayName("rechaza una contrasena sin caracter especial")
    void rechazaContrasenaSinSimbolo() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, "Secreto123")))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("especial");
    }

    @Test
    @DisplayName("no hashea la contrasena si la validacion falla")
    void noHasheaSiLaValidacionFalla() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar(datos(EMAIL, "corta")))
                .isInstanceOf(PasswordInseguraException.class);

        // Assert
        verify(passwordHasher, never()).hashear(anyString());
    }
}
