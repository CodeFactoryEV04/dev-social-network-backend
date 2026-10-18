package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.entity.TecnologiaRef;
import com.codefactory.dev_social_network.discusiones.repository.DiscussionRepository;
import com.codefactory.dev_social_network.discusiones.repository.TecnologiaRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private TecnologiaRefRepository tecnologiaRefRepository;

    private DiscussionService discussionService;

    private final UUID autorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        discussionService = new DiscussionService(discussionRepository, tecnologiaRefRepository);
    }

    @Test
    void shouldCreateDiscussionWhenTechnologyExists() throws Exception {

        UUID idTecnologia = UUID.randomUUID();
        TecnologiaRef tecnologia = newTecnologiaRef(idTecnologia, "Spring Boot");

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Como configurar Spring Security con JWT?");
        request.setContenido("Me sale un 403 incluso con un token valido.");
        request.setIdTecnologia(idTecnologia);

        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.of(tecnologia));
        when(discussionRepository.save(any(Discussion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DiscussionResponse response = discussionService.createDiscussion(autorId, request);

        assertThat(response.getTecnologia()).isEqualTo("Spring Boot");
        assertThat(response.getTitulo()).isEqualTo(request.getTitulo());
        assertThat(response.getContenido()).isEqualTo(request.getContenido());
        assertThat(response.getEstado()).isEqualTo(DiscussionStatus.ABIERTA);
        assertThat(response.getAutorId()).isEqualTo(autorId);

        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());

        Discussion saved = captor.getValue();
        assertThat(saved.getAutorId()).isEqualTo(autorId);
        assertThat(saved.getIdTecnologia()).isEqualTo(idTecnologia);
    }

    @Test
    void shouldThrowBusinessExceptionWhenTechnologyDoesNotExist() {

        UUID idTecnologia = UUID.randomUUID();

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Como configurar Spring Security con JWT?");
        request.setContenido("Me sale un 403 incluso con un token valido.");
        request.setIdTecnologia(idTecnologia);

        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discussionService.createDiscussion(autorId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("La tecnologia indicada no existe");

        verify(discussionRepository, never()).save(any());
    }

    // TecnologiaRef no tiene constructor publico (es de solo lectura, a proposito,
    // ver la explicacion en la propia clase). Para esta prueba, la unica forma
    // limpia de construir una instancia sin exponer un constructor publico que
    // nadie mas deberia usar es con reflexion, solo en el test.
    private TecnologiaRef newTecnologiaRef(UUID id, String nombre) throws Exception {
        TecnologiaRef tecnologia = new TecnologiaRef();
        setField(tecnologia, "id", id);
        setField(tecnologia, "nombreTecnologia", nombre);
        return tecnologia;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = TecnologiaRef.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

