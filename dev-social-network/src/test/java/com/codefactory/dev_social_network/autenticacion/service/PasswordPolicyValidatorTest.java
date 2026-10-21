package com.codefactory.dev_social_network.autenticacion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.codefactory.dev_social_network.shared.exception.AuthErrorCode;
import com.codefactory.dev_social_network.shared.exception.AuthException;

class PasswordPolicyValidatorTest {

    private final PasswordPolicyValidator validator = new PasswordPolicyValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "Abcdefg1!",
            "UnaClaveMuyLarga1@",
            "Segura#2026"
    })
    @DisplayName("acepta contrasenas que cumplen la politica")
    void aceptaContrasenasValidas(String password) {
        // Act y Assert
        assertThatCode(() -> validator.validar(password)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "abcdefg1!",      // sin mayuscula
            "ABCDEFG1!",      // sin minuscula
            "Abcdefgh!",      // sin digito
            "Abcdefg1",       // sin simbolo
            "Ab1!",           // menos de 8 caracteres
            "SoloLetras",     // sin digito ni simbolo
            "12345678!"       // sin letra
    })
    @DisplayName("rechaza contrasenas que no cumplen la politica")
    void rechazaContrasenasInvalidas(String password) {
        // Act y Assert
        assertThatThrownBy(() -> validator.validar(password))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("no cumple la política")
                .extracting(e -> ((AuthException) e).getErrorCode())
                .isEqualTo(AuthErrorCode.PASSWORD_NO_CUMPLE_POLITICA.name());
    }

    @Test
    @DisplayName("el error expone el codigo y el HTTP status correctos")
    void errorExponeCodigoYStatus() {
        // Act y Assert
        assertThatThrownBy(() -> validator.validar("corta"))
                .isInstanceOfSatisfying(AuthException.class, e -> {
                    assertThat(e.getErrorCode()).isEqualTo(AuthErrorCode.PASSWORD_NO_CUMPLE_POLITICA.name());

        // Assert
                    assertThat(e.getStatus()).isEqualTo(AuthErrorCode.PASSWORD_NO_CUMPLE_POLITICA.getStatus());
                    assertThat((List<?>) e.getDetails())
                            .anySatisfy(detalle -> assertThat(detalle.toString()).contains("mayúscula"));
                });
    }
}

