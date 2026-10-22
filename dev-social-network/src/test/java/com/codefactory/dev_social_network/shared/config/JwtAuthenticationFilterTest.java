package com.codefactory.dev_social_network.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.codefactory.dev_social_network.autenticacion.service.JwtProvider;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("sin cabecera Authorization sigue la cadena sin autenticar")
    void sinCabeceraNoAutentica() throws Exception {
        // Act
        filter.doFilter(request, response, chain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("una cabecera que no es Bearer no autentica")
    void cabeceraNoBearerNoAutentica() throws Exception {
        // Act
        request.addHeader("Authorization", "Basic abc123");
        filter.doFilter(request, response, chain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtProvider, never()).esTokenValido(anyString());
    }

    @Test
    @DisplayName("un token invalido no autentica pero deja pasar la peticion")
    void tokenInvalidoNoAutentica() throws Exception {
        // Act
        request.addHeader("Authorization", "Bearer token.invalido");

        // Assert
        when(jwtProvider.esTokenValido("token.invalido")).thenReturn(false);

        // Act
        filter.doFilter(request, response, chain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtProvider, never()).obtenerUsuarioId(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("un token valido autentica con el UUID del usuario como principal")
    void tokenValidoAutentica() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();

        // Act
        request.addHeader("Authorization", "Bearer token.valido");

        // Assert
        when(jwtProvider.esTokenValido("token.valido")).thenReturn(true);
        when(jwtProvider.obtenerUsuarioId("token.valido")).thenReturn(usuarioId);

        // Act
        filter.doFilter(request, response, chain);
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assert
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(usuarioId);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("el filtro delega siempre el trabajo a la cadena")
    void siempreContinuaLaCadena() throws Exception {
        // Act
        filter.doFilter(request, response, chain);

        // Assert
        verify(chain).doFilter(any(), any());
    }
}

