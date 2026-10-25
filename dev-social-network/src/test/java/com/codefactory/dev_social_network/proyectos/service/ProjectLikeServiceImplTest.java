package com.codefactory.dev_social_network.proyectos.service;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectLikeResponse;
import com.codefactory.dev_social_network.proyectos.dto.ProjectSummary;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectFinderPort;
import com.codefactory.dev_social_network.proyectos.interfaces.ProjectLikePort;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// MockitoExtension crea mocks nuevos antes de cada test y activa los stubs estrictos.
@ExtendWith(MockitoExtension.class)
@DisplayName("HU010 · ProjectLikeServiceImpl")
class ProjectLikeServiceImplTest {

    // ---- Datos de prueba tomados del Gherkin (Background) ----
    private static final Long PROYECTO_ID = 1024L;
    private static final String PROYECTO_TITULO = "DevConnect";
    private static final Long PROYECTO_INEXISTENTE_ID = 9999L;
    // UUID fijo = test repetible y mensajes de error legibles ("LuisDev")
    private static final UUID LUISDEV_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final ProjectSummary DEVCONNECT = new ProjectSummary(PROYECTO_ID, PROYECTO_TITULO);

    // ---- Dependencias simuladas: los PUERTOS, no JPA (Respetando la arquitectura) ----
    @Mock
    private ProjectFinderPort projectFinderPort;

    @Mock
    private ProjectLikePort projectLikePort;

    @InjectMocks
    private ProjectLikeServiceImpl service;

    @Nested
    @DisplayName("likeProject")
    class LikeProject {

        @Test
        @DisplayName("E1 - Like exitoso: devuelve el proyecto con 1 like y liked=true")
        void likeProject_proyectoExisteYSinLikePrevio_retornarRespuestaConContador() {
            // Given: El proyecto existe, el like se registra y ahora hay un 1 like.
            when(projectFinderPort.buscarResumenPorId(PROYECTO_ID)).thenReturn(Optional.of(DEVCONNECT));
            when(projectLikePort.registrarLike(PROYECTO_ID, LUISDEV_ID)).thenReturn(true);
            when(projectLikePort.contarLikes(PROYECTO_ID)).thenReturn(1L);
            // When: Llamada al metodo objeto de prueba
            ProjectLikeResponse response = service.likeProject(PROYECTO_ID, LUISDEV_ID);
            // Then: Los records comparan campo por campo
            assertThat(response).isEqualTo(
                    new ProjectLikeResponse(PROYECTO_ID, PROYECTO_TITULO, 1L, true)
            );
            // Aqui el orden importa: validar existencia, registar, contar.
            InOrder orden = inOrder(projectFinderPort, projectLikePort);
            orden.verify(projectFinderPort).buscarResumenPorId(PROYECTO_ID);
            orden.verify(projectLikePort).registrarLike(PROYECTO_ID, LUISDEV_ID);
            orden.verify(projectLikePort).contarLikes(PROYECTO_ID);
        }

        @Test
        @DisplayName("E2 · Like repetido: lanza 409 con el mensaje del criterio y no recuenta")
        void likeProject_usuarioYaDioLike_lanzaConflictoYNoCuenta() {
            // Given: el adaptador dice "no se creó" (ya existía)
            when(projectFinderPort.buscarResumenPorId(PROYECTO_ID)).thenReturn(Optional.of(DEVCONNECT));
            when(projectLikePort.registrarLike(PROYECTO_ID, LUISDEV_ID)).thenReturn(false);

            // When: assertThrows devuelve la excepción para inspeccionarla
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.likeProject(PROYECTO_ID, LUISDEV_ID));

            // Then: contrato completo del error (lo que usa GlobalExceptionHandler)
            assertThat(ex.getErrorCode()).isEqualTo("PROJECT_ALREADY_LIKED");
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
            // Mensaje literal porque el Gherkin dice "veo el mensaje ..."
            assertThat(ex.getMessage()).isEqualTo("Ya le haz dado like a este proyecto");
            // "el contador de likes del proyecto no cambia"
            verify(projectLikePort, never()).contarLikes(anyLong());
        }

        @Test
        @DisplayName("E3 · Proyecto inexistente: lanza 404 y no toca los likes")
        void likeProject_proyectoNoExiste_lanzaNoEncontradoSinTocarLikes() {
            // Given
            when(projectFinderPort.buscarResumenPorId(PROYECTO_INEXISTENTE_ID)).thenReturn(Optional.empty());

            // When
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.likeProject(PROYECTO_INEXISTENTE_ID, LUISDEV_ID));

            // Then
            assertThat(ex.getErrorCode()).isEqualTo("PROJECT_NOT_FOUND");
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(ex.getMessage()).isEqualTo("El proyecto no existe");
            // "lista de likes no cambia": ni se registra, ni se cuenta, ni se lista
            verifyNoInteractions(projectLikePort);
        }

        @Test
        @DisplayName("El contador refleja el total que reporta el puerto")
        void likeProject_proyectoConOtrosLikes_retornaTotalDelPuerto() {
            // Given: otros usuarios ya habían dado like
            when(projectFinderPort.buscarResumenPorId(PROYECTO_ID)).thenReturn(Optional.of(DEVCONNECT));
            when(projectLikePort.registrarLike(PROYECTO_ID, LUISDEV_ID)).thenReturn(true);
            when(projectLikePort.contarLikes(PROYECTO_ID)).thenReturn(42L);

            // When
            ProjectLikeResponse response = service.likeProject(PROYECTO_ID, LUISDEV_ID);

            // Then: el service no "suma 1" por su cuenta; confía en el conteo real
            assertThat(response.likesCount()).isEqualTo(42L);
        }

    }

    @Nested
    @DisplayName("getMyLikedProjects")
    class GetMyLikedProjects {

        @Test
        @DisplayName("E1 · El proyecto con like aparece en mi lista")
        void getMyLikedProjects_usuarioConLikes_retornaListaDelPuerto() {
            // Given
            LikedProjectResponse devconnect = new LikedProjectResponse(
                    PROYECTO_ID, PROYECTO_TITULO,
                    "https://github.com/luisdev/devconnect",
                    OffsetDateTime.parse("2026-10-12T10:00:00-05:00"));
            when(projectLikePort.listarPorUsuario(LUISDEV_ID)).thenReturn(List.of(devconnect));

            // When
            List<LikedProjectResponse> resultado = service.getMyLikedProjects(LUISDEV_ID);

            // Then
            assertThat(resultado).containsExactly(devconnect);
        }

        @Test
        @DisplayName("Extra · Usuario sin likes recibe lista vacía (no null)")
        void getMyLikedProjects_usuarioSinLikes_retornaListaVacia() {
            // Given
            when(projectLikePort.listarPorUsuario(LUISDEV_ID)).thenReturn(List.of());

            // When
            List<LikedProjectResponse> resultado = service.getMyLikedProjects(LUISDEV_ID);

            // Then
            assertThat(resultado).isNotNull().isEmpty();
        }
    }
}