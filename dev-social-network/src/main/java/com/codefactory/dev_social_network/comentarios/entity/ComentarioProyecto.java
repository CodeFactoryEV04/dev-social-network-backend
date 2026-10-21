package com.codefactory.dev_social_network.comentarios.entity;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interaccion_proyecto")
public class ComentarioProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "id_proyecto", nullable = false)
    private UUID idProyecto;

    @Column(name = "id_usuario", nullable = false)
    private UUID idUsuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    protected ComentarioProyecto() {
    }

    public ComentarioProyecto(UUID idProyecto, UUID idUsuario, String comentario) {
        this.idProyecto = idProyecto;
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.fechaCreacion = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdProyecto() {
        return idProyecto;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
}