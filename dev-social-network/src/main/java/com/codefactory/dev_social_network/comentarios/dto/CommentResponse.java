package com.codefactory.dev_social_network.comentarios.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID idUsuario,
        String username,
        String comentario,
        OffsetDateTime fechaCreacion
) {
}
