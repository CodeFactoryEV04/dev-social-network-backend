package com.codefactory.dev_social_network.comentarios.controller;

import com.codefactory.dev_social_network.autenticacion.service.JwtProvider;
import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.dto.PublishedCommentResponse;
import com.codefactory.dev_social_network.comentarios.service.ProjectCommentService;
import com.codefactory.dev_social_network.shared.config.SecurityConfig;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectCommentController.class)
@Import({SecurityConfig.class, ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class ProjectCommentControllerTest {

  private static final String TOKEN = "fake-jwt-token";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JsonMapper jsonMapper;

  @MockitoBean
  private ProjectCommentService service;

  @MockitoBean
  private JwtProvider jwtProvider;

  private UUID idUsuario;
  private UUID idProyecto;
  private CommentResponse commentResponse;

  @BeforeEach
  void setUp() {
    this.idUsuario = UUID.randomUUID();
    this.idProyecto = UUID.randomUUID();
    this.commentResponse = new CommentResponse(
        UUID.randomUUID(),
        idUsuario,
        "mock-username",
        "comentario",
        OffsetDateTime.parse("2026-01-15T10:30:45.123456789Z")
    );

    when(jwtProvider.esTokenValido(anyString())).thenReturn(true);
    when(jwtProvider.obtenerUsuarioId(anyString())).thenReturn(idUsuario);
  }

  private String jsonBody(String comentario) throws Exception {
    CreateCommentRequest request = new CreateCommentRequest();
    request.setComentario(comentario);
    return jsonMapper.writeValueAsString(request);
  }

  @Test
  void createCommentReturnsCreatedWithThePublishedComment() throws Exception {
    PublishedCommentResponse expected = new PublishedCommentResponse("Comentario publicado", commentResponse);

    when(service.createComment(eq(idUsuario), eq(idProyecto), any())).thenReturn(expected);

    mockMvc.perform(post("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN)
            .contentType("application/json")
            .content(jsonBody("comentario")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.mensaje", is("Comentario publicado")))
        .andExpect(jsonPath("$.comentario.id", is(commentResponse.id().toString())))
        .andExpect(jsonPath("$.comentario.idUsuario", is(commentResponse.idUsuario().toString())))
        .andExpect(jsonPath("$.comentario.username", is("mock-username")))
        .andExpect(jsonPath("$.comentario.comentario", is("comentario")))
        .andExpect(jsonPath("$.comentario.fechaCreacion", is(commentResponse.fechaCreacion().toString())));

    ArgumentCaptor<CreateCommentRequest> captor = ArgumentCaptor.forClass(CreateCommentRequest.class);
    verify(service).createComment(eq(idUsuario), eq(idProyecto), captor.capture());
    assertThat(captor.getValue().getComentario()).isEqualTo("comentario");
  }

  @Test
  void createCommentWithoutAuthenticationIsForbidden() throws Exception {
    mockMvc.perform(post("/api/v1/projects/{projectId}/comments", idProyecto)
            .contentType("application/json")
            .content(jsonBody("comentario")))
        .andExpect(status().isForbidden());

    verify(service, never()).createComment(any(), any(), any());
  }

  @Test
  void createCommentWithBlankComentarioIsBadRequest() throws Exception {
    mockMvc.perform(post("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN)
            .contentType("application/json")
            .content(jsonBody("   ")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.message", is("Request validation failed")))
        .andExpect(jsonPath("$.details[0].field", is("comentario")))
        .andExpect(jsonPath("$.details[0].message", is("El comentario no puede estar vacio")));

    verify(service, never()).createComment(any(), any(), any());
  }

  @Test
  void createCommentWithTooLongComentarioIsBadRequest() throws Exception {
    mockMvc.perform(post("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN)
            .contentType("application/json")
            .content(jsonBody("x".repeat(1001))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.details[0].field", is("comentario")))
        .andExpect(jsonPath("$.details[0].message", is("Comentario excede el limite de caracteres")));

    verify(service, never()).createComment(any(), any(), any());
  }

  @Test
  void createCommentWhenProjectDoesNotExistIsNotFound() throws Exception {
    when(service.createComment(eq(idUsuario), eq(idProyecto), any()))
        .thenThrow(new BusinessException("PROYECTO_NO_ENCONTRADO", "El proyecto no existe", HttpStatus.NOT_FOUND));

    mockMvc.perform(post("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN)
            .contentType("application/json")
            .content(jsonBody("comentario")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode", is("PROYECTO_NO_ENCONTRADO")))
        .andExpect(jsonPath("$.message", is("El proyecto no existe")));
  }

  @Test
  void listCommentsReturnsOkWithTheComments() throws Exception {
    when(service.listComments(idProyecto)).thenReturn(List.of(commentResponse));

    mockMvc.perform(get("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(commentResponse.id().toString())))
        .andExpect(jsonPath("$[0].idUsuario", is(commentResponse.idUsuario().toString())))
        .andExpect(jsonPath("$[0].username", is("mock-username")))
        .andExpect(jsonPath("$[0].comentario", is("comentario")))
        .andExpect(jsonPath("$[0].fechaCreacion", is(commentResponse.fechaCreacion().toString())));
  }

  @Test
  void listCommentsWithoutCommentsReturnsEmptyList() throws Exception {
    when(service.listComments(idProyecto)).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(0)));
  }

  @Test
  void listCommentsWhenProjectDoesNotExistIsNotFound() throws Exception {
    when(service.listComments(idProyecto))
        .thenThrow(new BusinessException("PROYECTO_NO_ENCONTRADO", "El proyecto no existe", HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/v1/projects/{projectId}/comments", idProyecto)
            .header("Authorization", "Bearer " + TOKEN))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode", is("PROYECTO_NO_ENCONTRADO")))
        .andExpect(jsonPath("$.message", is("El proyecto no existe")));
  }

}
