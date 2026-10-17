package com.codefactory.dev_social_network.consultas.interfaces;

import java.util.List;
import java.util.UUID;

import com.codefactory.dev_social_network.consultas.dto.FeedItem;

public interface FeedSource {

    List<FeedItem> recentByAuthors(List<UUID> authorIds, int limit);
}