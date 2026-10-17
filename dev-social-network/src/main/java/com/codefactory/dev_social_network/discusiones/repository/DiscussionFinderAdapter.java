package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DiscussionFinderAdapter implements DiscussionFinderPort {

    private final DiscussionRepository discussionRepository;

    public DiscussionFinderAdapter(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    @Override
    public Optional<Discussion> findById(Long discussionId) {
        return discussionRepository.findById(discussionId);
    }
}