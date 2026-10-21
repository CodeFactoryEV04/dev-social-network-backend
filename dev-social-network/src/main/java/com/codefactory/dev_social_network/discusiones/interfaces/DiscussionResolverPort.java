package com.codefactory.dev_social_network.discusiones.interfaces;

import java.util.UUID;

public interface DiscussionResolverPort {

    boolean markAsResolved(UUID discussionId);
}