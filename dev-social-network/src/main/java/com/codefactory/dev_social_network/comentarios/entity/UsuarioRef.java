package com.codefactory.dev_social_network.comentarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Referencia de solo lectura a la tabla "usuarios".
 *
 * Este módulo solo necesita el username para mostrarlo junto al
 * comentario. En vez de depender de la entidad interna del módulo
 * "usuarios" (monolito modular: cada módulo mantiene su propio
 * modelo de lo que necesita de otro), se mapea aquí solo lo
 * imprescindible. Sin constructor público ni setters.
 */
@Entity
@Table(name = "usuarios")
public class UsuarioRef {

    @Id
    private UUID id;

    @Column(name = "username")
    private String username;

    protected UsuarioRef() {
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
