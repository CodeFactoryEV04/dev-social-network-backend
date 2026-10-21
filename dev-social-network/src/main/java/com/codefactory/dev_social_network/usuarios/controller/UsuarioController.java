package com.codefactory.dev_social_network.usuarios.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.util.UUID;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioRequest;
import com.codefactory.dev_social_network.usuarios.dto.RegistroUsuarioResponse;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public UsuarioController(RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<RegistroUsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        UUID id = registrarUsuarioUseCase.registrar(new RegistrationData(
                request.username(),
                request.email(),
                request.nombre(),
                request.apellido(),
                request.contrasena()));
        RegistroUsuarioResponse response = new RegistroUsuarioResponse(id, "Cuenta creada exitosamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}