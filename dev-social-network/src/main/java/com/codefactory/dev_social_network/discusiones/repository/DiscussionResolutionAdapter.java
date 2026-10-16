package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class DiscussionResolutionAdapter implements DiscussionFinderPort, DiscussionResolverPort {

    private final DiscussionResolutionRepository discussionResolutionRepository;

    public DiscussionResolutionAdapter(DiscussionResolutionRepository discussionResolutionRepository) {
        this.discussionResolutionRepository = discussionResolutionRepository;
    }

    @Override
    public Optional<Discussion> findById(Long discussionId) {
        return discussionResolutionRepository.findById(discussionId);
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