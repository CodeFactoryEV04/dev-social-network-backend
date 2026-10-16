package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.shared.exception.SoloAutorPuedeResolverException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(1)
public class OnlyAuthorCanResolveRule implements DiscussionResolutionRule {

    @Override
    public void validate(Discussion discussion, UUID userId) {
        if (!discussion.getUserId().equals(userId)) {
            throw new SoloAutorPuedeResolverException();
        }
    }
}