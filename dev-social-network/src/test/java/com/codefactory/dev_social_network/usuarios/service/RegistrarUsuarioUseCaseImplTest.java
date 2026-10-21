package com.codefactory.dev_social_network.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.codefactory.dev_social_network.autenticacion.interfaces.CredencialService;
import com.codefactory.dev_social_network.shared.exception.EmailDuplicadoException;
import com.codefactory.dev_social_network.shared.exception.FormatoEmailInvalidoException;
import com.codefactory.dev_social_network.shared.exception.PasswordInseguraException;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.PasswordHasher;
import com.codefactory.dev_social_network.usuarios.interfaces.UserProfileRepositoryPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UsuarioRepositoryPort;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseImplTest {

    private static final String EMAIL = "ana@correo.com";
    private static final String PASSWORD = "Secreto1!";

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private CredencialService credencialService;
    @Mock
    private UserProfileRepositoryPort userProfileRepositoryPort;
    @Mock
    private PasswordHasher passwordHasher;

    private RegistrarUsuarioUseCaseImpl service;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        service = new RegistrarUsuarioUseCaseImpl(usuarioRepositoryPort, credencialService,
                userProfileRepositoryPort, passwordHasher);
        usuarioId = UUID.randomUUID();
    }

    private Usuario usuarioGuardado() {
        Usuario usuario = new Usuario("Ana", "Ruiz", EMAIL);
        ReflectionTestUtils.setField(usuario, "id", usuarioId);
        return usuario;
    }

    @Test
    @DisplayName("registra el usuario, su credencial y su perfil, y devuelve el id")
    void registraUsuarioCredencialYPerfil() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenReturn(usuarioGuardado());
        when(passwordHasher.hashear(PASSWORD)).thenReturn("hash-bcrypt");

        // Act
        UUID resultado = service.registrar(EMAIL, "Ana", "Ruiz", PASSWORD);

        // Assert
        assertThat(resultado).isEqualTo(usuarioId);
        verify(passwordHasher).hashear(PASSWORD);
        verify(credencialService).crearCredencialLocal(usuarioId, "hash-bcrypt");
        verify(userProfileRepositoryPort).guardar(any());
    }

    @Test
    @DisplayName("guarda el usuario con nombre, apellido y email")
    void guardaUsuarioConTodosLosDatos() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenReturn(usuarioGuardado());
        when(passwordHasher.hashear(PASSWORD)).thenReturn("hash-bcrypt");

        // Act
        service.registrar(EMAIL, "Ana", "Ruiz", PASSWORD);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana");
        assertThat(captor.getValue().getApellido()).isEqualTo("Ruiz");
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("rechaza un email con formato invalido")
    void rechazaEmailInvalido() {
        // Act y Assert
        assertThatThrownBy(() -> service.registrar("no-es-email", "Ana", "Ruiz", PASSWORD))
                .isInstanceOf(FormatoEmailInvalidoException.class);

        // Assert
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("rechaza un email ya registrado")
    void rechazaEmailDuplicado() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(true);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", PASSWORD))
                .isInstanceOf(EmailDuplicadoException.class);

        // Assert
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("rechaza una contrasena corta")
    void rechazaContrasenaCorta() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", "Aa1!"))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("8 caracteres");

        // Assert
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("rechaza una contrasena sin mayuscula")
    void rechazaContrasenaSinMayuscula() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", "secreto1!"))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("mayúscula");
    }

    @Test
    @DisplayName("rechaza una contrasena sin numero")
    void rechazaContrasenaSinNumero() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", "Secreto!"))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("número");
    }

    @Test
    @DisplayName("rechaza una contrasena sin caracter especial")
    void rechazaContrasenaSinSimbolo() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", "Secreto123"))
                .isInstanceOf(PasswordInseguraException.class)
                .hasMessageContaining("especial");
    }

    @Test
    @DisplayName("no hashea la contrasena si la validacion falla")
    void noHasheaSiLaValidacionFalla() {
        // Arrange
        when(usuarioRepositoryPort.existePorEmail(EMAIL)).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.registrar(EMAIL, "Ana", "Ruiz", "corta"))
                .isInstanceOf(PasswordInseguraException.class);

        // Assert
        verify(passwordHasher, never()).hashear(anyString());
    }
}

