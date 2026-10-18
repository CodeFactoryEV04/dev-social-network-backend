package main.java.com.codefactory.dev_social_network.Mensajeria.entity;

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

    @ManyToOne (optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private ConversationEntity conversation;

    @Column(name = "remitente_id", nullable = false)
    private UUID sender;

    @Column(name = "contenido", nullable = false, length = 2000)
    private String content;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime dateSent;

    protected MessageEntity() {
    }

    public MessageEntity(
            UUID sender,
            String content
    ) {
        this.sender = sender;
        this.content = content;
    }

    @PrePersist
    protected void antesDeGuardar() {
        if (dateSent == null) {
            dateSent = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }
}