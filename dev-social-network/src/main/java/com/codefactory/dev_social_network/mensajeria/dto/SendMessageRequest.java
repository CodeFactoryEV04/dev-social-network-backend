package com.codefactory.dev_social_network.mensajeria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        String receiverEmail,

        @NotBlank(message = "El mensaje no puede estar vacío")
        @Size(max = 2000, message = "Mensaje excede los 2000 caracteres")
        String content) {
}