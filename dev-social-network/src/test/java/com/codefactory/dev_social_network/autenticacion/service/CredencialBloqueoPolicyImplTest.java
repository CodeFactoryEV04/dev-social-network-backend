package com.codefactory.dev_social_network.autenticacion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.codefactory.dev_social_network.autenticacion.entity.CredencialEntity;

class CredencialBloqueoPolicyImplTest {

    private final CredencialBloqueoPolicyImpl policy = new CredencialBloqueoPolicyImpl();

    private CredencialEntity credencial(int intentos, LocalDateTime bloqueadoHasta) {
        CredencialEntity c = new CredencialEntity(UUID.randomUUID(), "LOCAL", "hash", null);
        c.setIntentosFallidos(intentos);
        c.setBloqueadoHasta(bloqueadoHasta);
        return c;
    }

    @Test
    @DisplayName("una credencial sin bloqueo no esta bloqueada")
    void sinBloqueoNoEstaBloqueada() {
        // Act
        var resultado = policy.estaBloqueada(credencial(0, null));

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("una credencial con bloqueo en el futuro esta bloqueada")
    void bloqueoEnFuturoEstaBloqueada() {
        // Act
        CredencialEntity c = credencial(3, LocalDateTime.now().plusMinutes(5));
        var resultado = policy.estaBloqueada(c);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("una credencial con bloqueo en el pasado ya no esta bloqueada")
    void bloqueoEnPasadoNoEstaBloqueada() {
        // Act
        CredencialEntity c = credencial(3, LocalDateTime.now().minusMinutes(1));
        var resultado = policy.estaBloqueada(c);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("un intento fallido incrementa el contador sin bloquear")
    void intentoFallidoIncrementaContador() {
        // Arrange
        CredencialEntity c = credencial(0, null);

        // Act
        policy.registrarIntentoFallido(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isEqualTo(1);
        assertThat(c.getBloqueadoHasta()).isNull();
    }

    @Test
    @DisplayName("al tercer intento fallido la cuenta queda bloqueada 5 minutos")
    void tercerIntentoBloquea() {
        // Arrange
        CredencialEntity c = credencial(2, null);

        // Act
        policy.registrarIntentoFallido(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isEqualTo(3);
        assertThat(c.getBloqueadoHasta()).isNotNull();
        assertThat(c.getBloqueadoHasta()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("reiniciar deja el contador en cero y quita el bloqueo")
    void reiniciarLimpiaElContador() {
        // Act
        CredencialEntity c = credencial(3, LocalDateTime.now().plusMinutes(5));
        policy.reiniciarIntentosFallidos(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isZero();
        assertThat(c.getBloqueadoHasta()).isNull();
    }

    @Test
    @DisplayName("un bloqueo vencido se libera automaticamente")
    void bloqueoVencidoSeLibera() {
        // Act
        CredencialEntity c = credencial(3, LocalDateTime.now().minusMinutes(1));
        policy.desbloquearSiVencio(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isZero();
        assertThat(c.getBloqueadoHasta()).isNull();
    }

    @Test
    @DisplayName("un bloqueo vigente no se toca al verificar el vencimiento")
    void bloqueoVigenteNoSeToca() {
        // Act
        CredencialEntity c = credencial(3, LocalDateTime.now().plusMinutes(5));
        policy.desbloquearSiVencio(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isEqualTo(3);
        assertThat(c.getBloqueadoHasta()).isNotNull();
    }

    @Test
    @DisplayName("una credencial nunca bloqueada no cambia al verificar el vencimiento")
    void credencialNoBloqueadaNoCambia() {
        // Arrange
        CredencialEntity c = credencial(1, null);

        // Act
        policy.desbloquearSiVencio(c);

        // Assert
        assertThat(c.getIntentosFallidos()).isEqualTo(1);
        assertThat(c.getBloqueadoHasta()).isNull();
    }
}

