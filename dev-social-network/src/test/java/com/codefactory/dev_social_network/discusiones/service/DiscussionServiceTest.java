package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private TecnologiaRefRepository tecnologiaRefRepository;

    private DiscussionService service;

    @BeforeEach
    void setUp() {
        service = new DiscussionService(discussionRepository, tecnologiaRefRepository);
    }

    @Test
    void deberiaCrearDiscusionCuandoLaTecnologiaExiste() throws Exception {
        UUID autorId = UUID.randomUUID();
        UUID idTecnologia = UUID.randomUUID();

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Como configurar CORS en Spring");
        request.setContenido("No logro que funcione con mi frontend");
        request.setIdTecnologia(idTecnologia);

        TecnologiaRef tecnologia = construirTecnologiaRef(idTecnologia, "Spring Boot");
        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.of(tecnologia));

        Discussion discusionGuardada = new Discussion(autorId, idTecnologia, request.getTitulo(), request.getContenido());
        when(discussionRepository.save(any())).thenReturn(discusionGuardada);

        DiscussionResponse response = service.createDiscussion(autorId, request);

        ArgumentCaptor<Discussion> captor = ArgumentCaptor.forClass(Discussion.class);
        verify(discussionRepository).save(captor.capture());
        assertThat(captor.getValue().getAutorId()).isEqualTo(autorId);
        assertThat(captor.getValue().getIdTecnologia()).isEqualTo(idTecnologia);
        assertThat(captor.getValue().getTitulo()).isEqualTo("Como configurar CORS en Spring");

        assertThat(response.getAutorId()).isEqualTo(autorId);
        assertThat(response.getTecnologia()).isEqualTo("Spring Boot");
        assertThat(response.getTitulo()).isEqualTo("Como configurar CORS en Spring");
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaTecnologiaNoExiste() {
        UUID autorId = UUID.randomUUID();
        UUID idTecnologia = UUID.randomUUID();

        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitulo("Titulo cualquiera");
        request.setContenido("Contenido cualquiera");
        request.setIdTecnologia(idTecnologia);

        when(tecnologiaRefRepository.findById(idTecnologia)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createDiscussion(autorId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La tecnologia indicada no existe");

        verify(discussionRepository, never()).save(any());
    }

    private TecnologiaRef construirTecnologiaRef(UUID id, String nombre) throws Exception {
        TecnologiaRef tecnologia = TecnologiaRef.class.getDeclaredConstructor().newInstance();
        Field idField = TecnologiaRef.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(tecnologia, id);
        Field nombreField = TecnologiaRef.class.getDeclaredField("nombreTecnologia");
        nombreField.setAccessible(true);
        nombreField.set(tecnologia, nombre);
        return tecnologia;
    }
}
