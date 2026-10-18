package com.codefactory.dev_social_network.Mensajeria.dto;

import java.util.UUID;

public record SendMessageRequest(
    @NotNull(message = "El mensaje debe tener un destinatario")
    UUID receiverId, 

    @NotNull(message = "El mensaje no puede estar vacío")
    @NotBlank(message = "El mensaje no puede estar en blanco")
    @Length(max = 2000, message = "El mensaje no puede tener más de 2000 caracteres")
    String content) {}
     