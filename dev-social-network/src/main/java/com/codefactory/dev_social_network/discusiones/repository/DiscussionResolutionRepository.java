package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DiscussionResolutionRepository extends Repository<Discussion, UUID> {

        @Modifying(flushAutomatically = true, clearAutomatically = true)
        @Query("""
                update Discussion d
                        set d.estado = :newStatus,
                        d.ultimaActualizacion = :updatedAt
                        where d.id = :id
                        and d.estado = :currentStatus
                """)
        int updateStatus(
                @Param("id") UUID id,
                @Param("currentStatus") DiscussionStatus currentStatus,
                @Param("newStatus") DiscussionStatus newStatus,
                @Param("updatedAt") OffsetDateTime updatedAt
        );
}