package com.codefactory.dev_social_network.Mensajeria.dto;

import java.util.UUID;

public class EnviarMensajeRequest {

    private UUID destinatarioId;
    private String contenido;

    public EnviarMensajeRequest() {
    }

    public UUID getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(UUID destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}