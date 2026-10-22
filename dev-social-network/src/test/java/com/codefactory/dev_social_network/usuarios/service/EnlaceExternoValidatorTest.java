package com.codefactory.dev_social_network.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.codefactory.dev_social_network.shared.exception.EnlaceInvalidoException;

class EnlaceExternoValidatorTest {

    private final EnlaceExternoValidator validator = new EnlaceExternoValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("un enlace de GitHub vacio es opcional y no se valida")
    void githubVacioEsOpcional(String link) {
        // Act y Assert
        assertThatCode(() -> validator.validarGithub(link)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://github.com/ana-ruiz",
            "http://github.com/ana-ruiz/",
            "  http://github.com/ana123  "
    })
    @DisplayName("acepta enlaces de GitHub con el patron esperado")
    void aceptaGithubValido(String link) {
        // Act y Assert
        assertThatCode(() -> validator.validarGithub(link)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://github.com/ana-ruiz",
            "http://github.com/ana_ruiz",
            "http://gitlab.com/ana-ruiz",
            "no-es-un-enlace"
    })
    @DisplayName("rechaza enlaces de GitHub que no cumplen el patron")
    void rechazaGithubInvalido(String link) {
        // Act y Assert
        assertThatThrownBy(() -> validator.validarGithub(link))
                .isInstanceOf(EnlaceInvalidoException.class)
                .hasMessageContaining("GitHub");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("un enlace de LinkedIn vacio es opcional y no se valida")
    void linkedinVacioEsOpcional(String link) {
        // Act y Assert
        assertThatCode(() -> validator.validarLinkedin(link)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("acepta un enlace de LinkedIn con el patron esperado")
    void aceptaLinkedinValido() {
        // Act y Assert
        assertThatCode(() -> validator.validarLinkedin("https://www.linkedin.com/in/ana-ruiz/"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://linkedin.com/in/ana-ruiz/",       // falta www
            "https://www.linkedin.com/in/ana-ruiz",     // falta la barra final
            "http://www.linkedin.com/in/ana-ruiz/",     // http en vez de https
            "https://www.linkedin.com/ana-ruiz/"        // falta /in/
    })
    @DisplayName("rechaza enlaces de LinkedIn que no cumplen el patron")
    void rechazaLinkedinInvalido(String link) {
        // Act y Assert
        assertThatThrownBy(() -> validator.validarLinkedin(link))
                .isInstanceOf(EnlaceInvalidoException.class)
                .hasMessageContaining("LinkedIn");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("un portafolio vacio es opcional y no se valida")
    void portafolioVacioEsOpcional(String link) {
        // Act y Assert
        assertThatCode(() -> validator.validarPortafolio(link)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://midominio.com",
            "http://mi-portafolio.dev/ana",
            "https://ana.github.io"
    })
    @DisplayName("acepta cualquier URL de portafolio con esquema y host")
    void aceptaPortafolioValido(String link) {
        // Act y Assert
        assertThatCode(() -> validator.validarPortafolio(link)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "no-es-una-url",
            "https://",
            "javascript:alert(1)"
    })
    @DisplayName("rechaza portafolios que no son URLs completas")
    void rechazaPortafolioInvalido(String link) {
        // Act y Assert
        assertThatThrownBy(() -> validator.validarPortafolio(link))
                .isInstanceOf(EnlaceInvalidoException.class)
                .hasMessageContaining("portafolio");
    }

    @Test
    @DisplayName("el mensaje de error de GitHub indica el patron esperado")
    void mensajeIndicaElPatron() {
        // Act y Assert
        assertThatThrownBy(() -> validator.validarGithub("https://github.com/ana"))
                .isInstanceOf(EnlaceInvalidoException.class)
                .hasMessageContaining("http://github.com/");
    }
}

