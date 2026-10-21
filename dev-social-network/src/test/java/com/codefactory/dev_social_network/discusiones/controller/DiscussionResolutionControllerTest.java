package com.codefactory.dev_social_network.discusiones.controller;

import com.codefactory.dev_social_network.autenticacion.service.JwtProvider;
import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.ResolveDiscussionUseCase;
import com.codefactory.dev_social_network.shared.config.CurrentUserPort;
import com.codefactory.dev_social_network.shared.config.SecurityConfig;
import com.codefactory.dev_social_network.shared.exception.DiscusionNoEncontradaException;
import com.codefactory.dev_social_network.shared.exception.DiscusionYaResueltaException;
import com.codefactory.dev_social_network.shared.exception.SoloAutorPuedeResolverException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DiscussionResolutionController.class)
@Import({SecurityConfig.class, ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class DiscussionResolutionControllerTest {

  private static final String TOKEN = "fake-jwt-token";

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  private ResolveDiscussionUseCase useCase;

  @MockitoBean
  private CurrentUserPort currentUserPort;

  @MockitoBean
  private JwtProvider jwtProvider;

  private UUID userId;

  private ResolveDiscussionResponse response;

  @BeforeEach
  void setUp() {
    this.userId = UUID.randomUUID();
    this.response = new ResolveDiscussionResponse(
        1L,
        DiscussionStatus.RESOLVED,
        "Mocked message"
    );

  }

  @Test
  void resolveResponseCorrectly() throws Exception {
    when(currentUserPort.currentUserId()).thenReturn(userId);
    when(useCase.resolve(userId, response.id())).thenReturn(response);
    when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
    when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(userId);

    mockMvc.perform(patch("/api/v1/discussions/{id}/resolve", response.id())
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.id()))
        .andExpect(jsonPath("$.status", is(response.status().name())))
        .andExpect(jsonPath("$.message", is(response.message())));
  }

  @Test
  void resolveWithOutAuthentication() throws Exception {
    mockMvc.perform(patch("/api/v1/discussions/{id}/resolve", response.id()))
        .andExpect(status().isForbidden());

    verify(currentUserPort, never()).currentUserId();
    verify(useCase, never()).resolve(any(), any());
    verify(jwtProvider, never()).esTokenValido(anyString());
    verify(jwtProvider, never()).obtenerUsuarioId(anyString());
  }

  @Test
  void alreadyResolvedDiscussion() throws Exception {
    when(currentUserPort.currentUserId()).thenReturn(userId);
    when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
    when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(userId);
    when(useCase.resolve(userId, response.id())).thenThrow(new DiscusionYaResueltaException());

    mockMvc.perform(patch("/api/v1/discussions/{id}/resolve", response.id())
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode", is("DISCUSION_YA_RESUELTA")))
        .andExpect(jsonPath("$.message", is("Está discusión ya ha sido resuelta")));
  }

  @Test
  void nonExistentDiscussion() throws  Exception {
    when(currentUserPort.currentUserId()).thenReturn(userId);
    when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
    when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(userId);
    when(useCase.resolve(userId, response.id())).thenThrow(new DiscusionNoEncontradaException());

    mockMvc.perform(patch("/api/v1/discussions/{id}/resolve", response.id())
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode", is("DISCUSION_NO_ENCONTRADA")))
        .andExpect(jsonPath("$.message", is("Está discusión no existe")));
  }

  @Test
  void markAsResolvedDiscussionOfOtherUser() throws Exception {
    when(currentUserPort.currentUserId()).thenReturn(userId);
    when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
    when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(userId);
    when(useCase.resolve(userId, response.id())).thenThrow(new SoloAutorPuedeResolverException());

    mockMvc.perform(patch("/api/v1/discussions/{id}/resolve", response.id())
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode", is("SOLO_AUTOR_PUEDE_RESOLVER")))
        .andExpect(jsonPath("$.message", is("Solo el autor puede marcar como resuelta la discusión")));
  }

}
