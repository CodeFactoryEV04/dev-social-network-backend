package com.codefactory.dev_social_network.comentarios.entity;
 
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
    Referencia de solo lectura a la tabla real "proyectos".
    No usamos la entidad Project del módulo "proyectos" porque hoy
    esa clase está mapeada incorrectamente (@Table("projects"), id
    Long) y no coincide con la tabla real de Postgres ("proyectos",
    id UUID). Mientras ese problema no se corrija en su propio
    módulo, esta clase mínima nos permite validar que un proyecto
    existe sin heredar ese error. Solo lectura: no tiene
    constructor público ni setters, porque este módulo nunca debe
    crear ni modificar proyectos, solo consultarlos.
    
    Borrar esta clase y volver a usar Project directamente el día
    que el módulo "proyectos" corrija su mapeo.

**/

@Entity
@Table(name = "proyectos")
public class ProyectoRef {

    @Id
    private java.util.UUID id;

    protected ProyectoRef() {
    }

    public java.util.UUID getId() {
        return id;
    }
    
}