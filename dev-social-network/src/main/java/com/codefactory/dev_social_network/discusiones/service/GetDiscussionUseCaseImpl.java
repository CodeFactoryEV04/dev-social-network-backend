package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.DiscussionDetailResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.GetDiscussionUseCase;
import com.codefactory.dev_social_network.shared.exception.DiscusionNoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetDiscussionUseCaseImpl implements GetDiscussionUseCase {

    private final DiscussionFinderPort discussionFinderPort;

    public GetDiscussionUseCaseImpl(DiscussionFinderPort discussionFinderPort) {
        this.discussionFinderPort = discussionFinderPort;
    }

    @Override
    @Transactional(readOnly = true)
    public DiscussionDetailResponse getById(UUID discussionId) {
        Discussion discussion = discussionFinderPort.findById(discussionId)
                .orElseThrow(DiscusionNoEncontradaException::new);

        return new DiscussionDetailResponse(
                discussion.getId(),
                discussion.getTitulo(),
                discussion.getEstado(),
                discussion.getAutorId()
        );
    }
}