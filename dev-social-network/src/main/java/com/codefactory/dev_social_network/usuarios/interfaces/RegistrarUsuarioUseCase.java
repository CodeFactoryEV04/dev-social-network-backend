package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;

public interface RegistrarUsuarioUseCase {

    UUID registrar(RegistrationData data);
}