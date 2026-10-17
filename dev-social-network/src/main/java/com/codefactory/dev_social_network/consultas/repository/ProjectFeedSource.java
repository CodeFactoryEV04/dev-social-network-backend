package com.codefactory.dev_social_network.consultas.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.consultas.dto.FeedItem;
import com.codefactory.dev_social_network.consultas.dto.FeedItemType;
import com.codefactory.dev_social_network.consultas.interfaces.FeedSource;

import jakarta.persistence.EntityManager;

@Component
public class ProjectFeedSource implements FeedSource {

    private final EntityManager entityManager;

    public ProjectFeedSource(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<FeedItem> recentByAuthors(List<UUID> authorIds, int limit) {
        List<Object[]> rows = entityManager.createQuery(
                        "select p.id, p.title, p.userId, p.createdAt "
                      + "from Project p "
                      + "where p.userId in :authorIds "
                      + "order by p.createdAt desc", Object[].class)
                .setParameter("authorIds", authorIds)
                .setMaxResults(limit)
                .getResultList();

        return rows.stream()
                .map(r -> new FeedItem(
                        FeedItemType.PROJECT,
                        (Long) r[0],
                        (String) r[1],
                        (UUID) r[2],
                        (OffsetDateTime) r[3]))
                .toList();
    }
}