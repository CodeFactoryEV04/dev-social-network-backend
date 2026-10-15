package com.codefactory.dev_social_network.autenticacion.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class AuthEventPublisherTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AuthEventPublisher authEventPublisher;

    @Test
    @DisplayName("publica un SesionIniciadaEvent con los datos del inicio de sesion")
    void publicaSesionIniciada() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime antes = LocalDateTime.now();

        // Act
        authEventPublisher.publicarSesionIniciada(usuarioId, "ana@correo.com", "127.0.0.1", "JUnit");

        // Assert
        ArgumentCaptor<SesionIniciadaEvent> captor = ArgumentCaptor.forClass(SesionIniciadaEvent.class);
        verify(publisher).publishEvent(captor.capture());
        SesionIniciadaEvent evento = captor.getValue();
        assertThat(evento.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(evento.getEmail()).isEqualTo("ana@correo.com");
        assertThat(evento.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(evento.getUserAgent()).isEqualTo("JUnit");
        assertThat(evento.getOcurridoEn()).isAfterOrEqualTo(antes);
    }

    @Test
    @DisplayName("publica un CuentaBloqueadaEvent con los intentos y la fecha de desbloqueo")
    void publicaCuentaBloqueada() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime bloqueadoHasta = LocalDateTime.now().plusMinutes(15);

        // Act
        authEventPublisher.publicarCuentaBloqueada(usuarioId, "ana@correo.com", 5, bloqueadoHasta);

        // Assert
        ArgumentCaptor<CuentaBloqueadaEvent> captor = ArgumentCaptor.forClass(CuentaBloqueadaEvent.class);
        verify(publisher).publishEvent(captor.capture());
        CuentaBloqueadaEvent evento = captor.getValue();
        assertThat(evento.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(evento.getEmail()).isEqualTo("ana@correo.com");
        assertThat(evento.getIntentosFallidos()).isEqualTo(5);
        assertThat(evento.getBloqueadoHasta()).isEqualTo(bloqueadoHasta);
    }
}
