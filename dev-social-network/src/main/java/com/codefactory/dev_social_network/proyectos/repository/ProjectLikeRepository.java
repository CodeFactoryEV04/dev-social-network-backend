package com.codefactory.dev_social_network.proyectos.repository;

import com.codefactory.dev_social_network.proyectos.entity.ProjectLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {

    boolean existsByProject_IdAndUsuarioId(Long projectId, UUID usuarioId);

    long countByProject_Id(Long projectId);

    @Query("select l from ProjectLike l join fetch l.project "
            + "where l.usuarioId = :usuarioId order by l.createdAt desc")
    List<ProjectLike> findAllByUsuarioIdWithProject(@Param("usuarioId") UUID usuarioId);
}