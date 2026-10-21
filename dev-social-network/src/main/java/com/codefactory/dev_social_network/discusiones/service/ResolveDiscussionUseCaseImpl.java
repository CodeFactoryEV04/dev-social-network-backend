package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import com.codefactory.dev_social_network.discusiones.interfaces.ResolveDiscussionUseCase;
import com.codefactory.dev_social_network.shared.exception.DiscusionNoEncontradaException;
import com.codefactory.dev_social_network.shared.exception.DiscusionYaResueltaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ResolveDiscussionUseCaseImpl implements ResolveDiscussionUseCase {

    private static final String SUCCESS_MESSAGE = "La discusión ha sido marcada como resuelta";

    private final DiscussionFinderPort discussionFinderPort;
    private final DiscussionResolverPort discussionResolverPort;
    private final List<DiscussionResolutionRule> rules;

    public ResolveDiscussionUseCaseImpl(
            DiscussionFinderPort discussionFinderPort,
            DiscussionResolverPort discussionResolverPort,
            List<DiscussionResolutionRule> rules
    ) {
        this.discussionFinderPort = discussionFinderPort;
        this.discussionResolverPort = discussionResolverPort;
        this.rules = rules;
    }

    @Override
    @Transactional
    public ResolveDiscussionResponse resolve(UUID autorId, UUID discussionId) {

        Discussion discussion = discussionFinderPort.findById(discussionId)
                .orElseThrow(DiscusionNoEncontradaException::new);

        for (DiscussionResolutionRule rule : rules) {
            rule.validate(discussion, autorId);
        }

        if (!discussionResolverPort.markAsResolved(discussionId)) {
            throw new DiscusionYaResueltaException();
        }

        return new ResolveDiscussionResponse(
                discussionId,
                DiscussionStatus.RESUELTA,
                SUCCESS_MESSAGE
        );
    }
}
