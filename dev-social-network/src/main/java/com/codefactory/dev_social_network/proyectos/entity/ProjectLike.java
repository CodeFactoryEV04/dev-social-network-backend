package com.codefactory.dev_social_network.proyectos.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "project_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_like_project_user",
                columnNames = {"project_id", "usuario_id"}
        )
)
public class ProjectLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Referencia por id al módulo usuarios (sin relación JPA entre módulos)
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ProjectLike() {
        // Requerido por JPA
    }

    public ProjectLike(Project project, UUID usuarioId) {
        this.project = project;
        this.usuarioId = usuarioId;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public UUID getUsuarioId() { return usuarioId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}