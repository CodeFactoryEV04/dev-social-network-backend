package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.shared.exception.DiscusionYaResueltaException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(2)
public class DiscussionNotAlreadyResolvedRule implements DiscussionResolutionRule {

    @Override
    public void validate(Discussion discussion, UUID userId) {
        if (discussion.getStatus() == DiscussionStatus.RESOLVED) {
            throw new DiscusionYaResueltaException();
        }
    }
}