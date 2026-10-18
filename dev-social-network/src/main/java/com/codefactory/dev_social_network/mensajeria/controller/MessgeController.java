package com.codefactory.dev_social_network.Mensajeria.controller;

import com.codefactory.dev_social_network.Mensajeria.dto.EnviarMensajeRequest;
import com.codefactory.dev_social_network.Mensajeria.dto.MensajeResponse;
import com.codefactory.dev_social_network.Mensajeria.service.MensajeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mensajes")
@Tag(
        name = "Mensajería",
        description = "Operaciones para enviar mensajes privados"
)
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    @Operation(
            summary = "Enviar un mensaje privado",
            description = "Envía un mensaje al usuario indicado por el desarrollador autenticado"
    )
    public ResponseEntity<MensajeResponse> enviarMensaje(
            @AuthenticationPrincipal UUID remitenteId,
            @Valid @RequestBody EnviarMensajeRequest request
    ) {
        MensajeResponse response =
                mensajeService.enviarMensaje(remitenteId, request);

        return ResponseEntity.ok(response);
    }
}