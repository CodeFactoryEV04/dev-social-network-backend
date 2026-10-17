package com.codefactory.dev_social_network.consultas.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.consultas.dto.FeedItem;
import com.codefactory.dev_social_network.consultas.interfaces.FeedSource;
import com.codefactory.dev_social_network.consultas.interfaces.FollowedAuthorsPort;
import com.codefactory.dev_social_network.consultas.interfaces.GetFeedUseCase;

@Service
public class GetFeedUseCaseImpl implements GetFeedUseCase {

    private static final int FEED_LIMIT = 20;

    private final FollowedAuthorsPort followedAuthorsPort;
    private final List<FeedSource> sources;

    public GetFeedUseCaseImpl(FollowedAuthorsPort followedAuthorsPort,
                              List<FeedSource> sources) {
        this.followedAuthorsPort = followedAuthorsPort;
        this.sources = sources;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedItem> feedFor(UUID userId) {
        List<UUID> followed = followedAuthorsPort.findFollowedIds(userId);
        if (followed.isEmpty()) {
            return List.of();
        }

        return sources.stream()
                .flatMap(source -> source.recentByAuthors(followed, FEED_LIMIT).stream())
                .sorted(Comparator.comparing(FeedItem::createdAt).reversed())
                .limit(FEED_LIMIT)
                .toList();
    }
}