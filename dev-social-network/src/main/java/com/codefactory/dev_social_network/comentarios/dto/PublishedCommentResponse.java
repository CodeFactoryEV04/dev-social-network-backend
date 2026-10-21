package com.codefactory.dev_social_network.comentarios.dto;

public record PublishedCommentResponse(
        String mensaje,
        CommentResponse comentario
) {
}
