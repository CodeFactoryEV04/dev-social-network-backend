package com.codefactory.dev_social_network.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("el hash es distinto de la contrasena en plano")
    void hashDifiereDeLaContrasena() {
        // Act
        String hash = hasher.hashear("Secreto1!");

        // Assert
        assertThat(hash).isNotEqualTo("Secreto1!");
        assertThat(hash).startsWith("$2");
    }

    @Test
    @DisplayName("el hash generado valida contra la contrasena original")
    void hashValidaContraOriginal() {
        // Act
        String hash = hasher.hashear("Secreto1!");
        var resultado = encoder.matches("Secreto1!", hash);

        // Assert
        assertThat(resultado).isTrue();

        // Act
        var resultado2 = encoder.matches("OtraClave1!", hash);

        // Assert
        assertThat(resultado2).isFalse();
    }

    @Test
    @DisplayName("dos hashes de la misma contrasena son distintos por el salt")
    void dosHashesDifieren() {
        // Act
        String primero = hasher.hashear("Secreto1!");
        String segundo = hasher.hashear("Secreto1!");

        // Assert
        assertThat(primero).isNotEqualTo(segundo);
    }
}

