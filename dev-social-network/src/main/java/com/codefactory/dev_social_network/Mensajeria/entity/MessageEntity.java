package com.codefactory.dev_social_network.Mensajeria.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensajes_privados")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "remitente_id", nullable = false)
    private UUID remitenteId;

    @Column(name = "destinatario_id", nullable = false)
    private UUID destinatarioId;

    @Column(name = "contenido", nullable = false, length = 2000)
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    protected MessageEntity() {
    }

    public MessageEntity(
            UUID remitenteId,
            UUID destinatarioId,
            String contenido
    ) {
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
    }

    @PrePersist
    protected void antesDeGuardar() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRemitenteId() {
        return remitenteId;
    }

    public UUID getDestinatarioId() {
        return destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }
}