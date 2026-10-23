package com.codefactory.dev_social_network.mensajeria.service;

import com.codefactory.dev_social_network.shared.exception.AuthenticationRequiredException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextAuthenticatedUserProviderTest {

    private SecurityContextAuthenticatedUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SecurityContextAuthenticatedUserProvider();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deberiaDevolverElIdDelUsuarioAutenticado() {
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of()));

        assertThat(provider.currentUserId()).isEqualTo(userId);
    }

    @Test
    void deberiaLanzarExcepcionSiNoHayAutenticacion() {
        assertThatThrownBy(() -> provider.currentUserId())
                .isInstanceOf(AuthenticationRequiredException.class)
                .hasMessage("Debes iniciar sesión para continuar");
    }

    @Test
    void deberiaLanzarExcepcionSiElPrincipalNoEsUnUuid() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuario", null, List.of()));

        assertThatThrownBy(() -> provider.currentUserId())
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void deberiaLanzarExcepcionSiLaAutenticacionNoEstaAutenticada() {
        // El constructor de dos argumentos deja el token sin autenticar.
        Authentication notAuthenticated = new UsernamePasswordAuthenticationToken(
                UUID.randomUUID(), null);
        SecurityContextHolder.getContext().setAuthentication(notAuthenticated);

        assertThatThrownBy(() -> provider.currentUserId())
                .isInstanceOf(AuthenticationRequiredException.class);
    }
}
