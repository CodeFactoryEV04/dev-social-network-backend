package com.codefactory.dev_social_network.discusiones.dto;

import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DiscussionResponse {

    private UUID id;
    private UUID autorId;
    private String tecnologia;
    private String titulo;
    private String contenido;
    private DiscussionStatus estado;
    private OffsetDateTime fechaCreacion;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public void setAutorId(UUID autorId) {
        this.autorId = autorId;
    }

    public String getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(String tecnologia) {
        this.tecnologia = tecnologia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public DiscussionStatus getEstado() {
        return estado;
    }

    public void setEstado(DiscussionStatus estado) {
        this.estado = estado;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

