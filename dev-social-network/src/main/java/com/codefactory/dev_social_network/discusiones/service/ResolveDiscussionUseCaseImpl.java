package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.ResolveDiscussionUseCase;
import com.codefactory.dev_social_network.discusiones.repository.DiscussionResolutionRepository;
import com.codefactory.dev_social_network.shared.exception.DiscusionNoEncontradaException;
import com.codefactory.dev_social_network.shared.exception.DiscusionYaResueltaException;
import com.codefactory.dev_social_network.shared.exception.SoloAutorPuedeResolverException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ResolveDiscussionUseCaseImpl implements ResolveDiscussionUseCase {

    private static final String SUCCESS_MESSAGE = "La discusión ha sido marcada como resuelta";

    private final DiscussionResolutionRepository discussionResolutionRepository;

    public ResolveDiscussionUseCaseImpl(DiscussionResolutionRepository discussionResolutionRepository) {
        this.discussionResolutionRepository = discussionResolutionRepository;
    }

    @Override
    @Transactional
    public ResolveDiscussionResponse resolve(UUID userId, Long discussionId) {

        Discussion discussion = discussionResolutionRepository.findById(discussionId)
                .orElseThrow(DiscusionNoEncontradaException::new);

        if (!discussion.getUserId().equals(userId)) {
            throw new SoloAutorPuedeResolverException();
        }

        if (discussion.getStatus() == DiscussionStatus.RESOLVED) {
            throw new DiscusionYaResueltaException();
        }

        int updatedRows = discussionResolutionRepository.updateStatus(
                discussionId,
                DiscussionStatus.OPEN,
                DiscussionStatus.RESOLVED,
                OffsetDateTime.now()
        );

        if (updatedRows == 0) {
            throw new DiscusionYaResueltaException();
        }

        return new ResolveDiscussionResponse(
                discussionId,
                DiscussionStatus.RESOLVED,
                SUCCESS_MESSAGE
        );
    }
}