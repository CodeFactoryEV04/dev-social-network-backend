package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.CreateDiscussionRequest;
import com.codefactory.dev_social_network.discusiones.dto.DiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.TecnologiaRef;
import com.codefactory.dev_social_network.discusiones.repository.DiscussionRepository;
import com.codefactory.dev_social_network.discusiones.repository.TecnologiaRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final TecnologiaRefRepository tecnologiaRefRepository;

    public DiscussionService(
            DiscussionRepository discussionRepository,
            TecnologiaRefRepository tecnologiaRefRepository
    ) {
        this.discussionRepository = discussionRepository;
        this.tecnologiaRefRepository = tecnologiaRefRepository;
    }

    @Transactional
    public DiscussionResponse createDiscussion(UUID autorId, CreateDiscussionRequest request) {

        TecnologiaRef tecnologia = tecnologiaRefRepository.findById(request.getIdTecnologia())
                .orElseThrow(() -> new BusinessException(
                        "TECNOLOGIA_INVALIDA",
                        "La tecnologia indicada no existe",
                        HttpStatus.BAD_REQUEST
                ));

        Discussion discussion = new Discussion(
                autorId,
                tecnologia.getId(),
                request.getTitulo(),
                request.getContenido()
        );

        Discussion discusionGuardada = discussionRepository.save(discussion);

        return mapToResponse(discusionGuardada, tecnologia);
    }

    private DiscussionResponse mapToResponse(Discussion discussion, TecnologiaRef tecnologia) {

        DiscussionResponse response = new DiscussionResponse();

        response.setId(discussion.getId());
        response.setAutorId(discussion.getAutorId());
        response.setTecnologia(tecnologia.getNombreTecnologia());
        response.setTitulo(discussion.getTitulo());
        response.setContenido(discussion.getContenido());
        response.setEstado(discussion.getEstado());
        response.setFechaCreacion(discussion.getFechaCreacion());

        return response;
    }
}
