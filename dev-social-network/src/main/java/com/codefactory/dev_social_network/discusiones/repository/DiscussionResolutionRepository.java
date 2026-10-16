package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface DiscussionResolutionRepository extends Repository<Discussion, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Discussion d
               set d.status = :newStatus,
                   d.updatedAt = :updatedAt
             where d.id = :id
               and d.status = :currentStatus
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("currentStatus") DiscussionStatus currentStatus,
            @Param("newStatus") DiscussionStatus newStatus,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}