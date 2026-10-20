package com.codefactory.dev_social_network.consultas.interfaces;

import java.util.List;
import java.util.UUID;

import com.codefactory.dev_social_network.consultas.dto.FeedItem;

public interface GetFeedUseCase {

    List<FeedItem> feedFor(UUID userId);
}