package com.codefactory.dev_social_network.shared.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RootRedirectController")
class RootRedirectControllerTest {

    @Test
    @DisplayName("la raiz redirige a la interfaz de Swagger")
    void redirigeASwagger() {
        // Arrange
        RootRedirectController controller = new RootRedirectController();

        // Act
        String resultado = controller.redirectToSwagger();

        // Assert
        assertThat(resultado).isEqualTo("redirect:/swagger-ui/index.html");
    }
}
