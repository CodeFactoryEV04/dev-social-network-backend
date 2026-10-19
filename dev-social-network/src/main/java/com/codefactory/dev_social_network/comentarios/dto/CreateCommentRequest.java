package com.codefactory.dev_social_network.comentarios.dto;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comentario;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
