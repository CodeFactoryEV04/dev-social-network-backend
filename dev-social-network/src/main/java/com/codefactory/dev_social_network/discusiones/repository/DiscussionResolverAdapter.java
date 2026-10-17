package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DiscussionResolverAdapter implements DiscussionResolverPort {

    private final DiscussionResolutionRepository discussionResolutionRepository;

    public DiscussionResolverAdapter(DiscussionResolutionRepository discussionResolutionRepository) {
        this.discussionResolutionRepository = discussionResolutionRepository;
    }

    @Override
    public boolean markAsResolved(Long discussionId) {
        return discussionResolutionRepository.updateStatus(
                discussionId,
                DiscussionStatus.OPEN,
                DiscussionStatus.RESOLVED,
                OffsetDateTime.now()
        ) > 0;
    }
}