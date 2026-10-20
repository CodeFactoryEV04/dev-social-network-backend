package com.codefactory.dev_social_network.discusiones.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tecnologias")
public class TecnologiaRef {

    @Id
    private UUID id;

    @Column(name = "nombre_tecnologia", nullable = false)
    private String nombreTecnologia;

    protected TecnologiaRef() {
    }

    public UUID getId() {
        return id;
    }

    public String getNombreTecnologia() {
        return nombreTecnologia;
    }
}