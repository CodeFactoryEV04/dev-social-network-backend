package com.codefactory.dev_social_network.discusiones.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "discusiones")
public class Discussion {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "autor_id", nullable = false)
  private UUID autorId;

  @Column(name = "id_tecnologia", nullable = false)
  private UUID idTecnologia;

  @Column(name = "titulo", nullable = false, length = 150)
  private String titulo;

  @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
  private String contenido;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private DiscussionStatus estado;

  @Column(name = "fecha_creacion", nullable = false)
  private OffsetDateTime fechaCreacion;

  @Column(name = "ultima_actualizacion", nullable = false)
  private OffsetDateTime ultimaActualizacion;

  protected Discussion() {
  }

  public Discussion(UUID autorId, UUID idTecnologia, String titulo, String contenido) {
    OffsetDateTime now = OffsetDateTime.now();
    this.autorId = autorId;
    this.idTecnologia = idTecnologia;
    this.titulo = titulo;
    this.contenido = contenido;
    this.estado = DiscussionStatus.ABIERTA;
    this.fechaCreacion = now;
    this.ultimaActualizacion = now;
  }

  public UUID getId() {
    return id;
  }

  public UUID getAutorId() {
    return autorId;
  }

  public UUID getIdTecnologia() {
    return idTecnologia;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getContenido() {
    return contenido;
  }

  public DiscussionStatus getEstado() {
    return estado;
  }

  public OffsetDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public OffsetDateTime getUltimaActualizacion() {
    return ultimaActualizacion;
  }

  public void setStatus(DiscussionStatus status) {
    this.estado = status;
  }

  @Override
  public String toString() {
    String contentPreview = contenido != null && contenido.length() > 50
        ? contenido.substring(0, 50) + "..."
        : contenido;
    return "Discussion{" +
        "id=" + id +
        ", userId=" + autorId +
        ", technologyId=" + idTecnologia +
        ", title='" + titulo + '\'' +
        ", content='" + contentPreview + '\'' +
        ", status=" + estado +
        ", createdAt=" + fechaCreacion +
        ", updatedAt=" + ultimaActualizacion +
        '}';
  }
}
