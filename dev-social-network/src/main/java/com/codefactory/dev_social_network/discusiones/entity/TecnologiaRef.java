package com.codefactory.dev_social_network.discusiones.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Referencia de solo lectura a la tabla real "tecnologias".
 *
 * No usamos ninguna entidad del módulo catalogoTech porque hoy
 * TecnologiesEntity está mapeada incorrectamente (no coincide con
 * la tabla real "tecnologias" en Postgres). Mientras ese problema
 * no se corrija en su propio módulo, esta clase mínima nos
 * permite validar que una tecnología existe y obtener su nombre,
 * sin heredar ese error. Solo lectura: sin constructor público ni
 * setters, porque este módulo nunca debe crear ni modificar
 * tecnologías, solo consultarlas.
 *
 * Borrar esta clase y volver a usar la entidad de catalogoTech
 * directamente el día que ese módulo corrija su mapeo.
 */
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
