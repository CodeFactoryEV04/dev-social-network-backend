package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.autenticacion.interfaces.CredencialService;
import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.interfaces.PasswordHasher;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;
import com.codefactory.dev_social_network.usuarios.interfaces.UserRegistrationPort;

@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UserRegistrationPort userRegistrationPort;
    private final CredencialService credencialService;
    private final PasswordHasher passwordHasher;
    private final List<RegistrationRule> rules;

    public RegistrarUsuarioUseCaseImpl(UserRegistrationPort userRegistrationPort,
                                       CredencialService credencialService,
                                       PasswordHasher passwordHasher,
                                       List<RegistrationRule> rules) {
        this.userRegistrationPort = userRegistrationPort;
        this.credencialService = credencialService;
        this.passwordHasher = passwordHasher;
        this.rules = rules;
    }

    @Override
    @Transactional
    public UUID registrar(RegistrationData data) {

        for (RegistrationRule rule : rules) {
            rule.validate(data);
        }

        UUID userId = userRegistrationPort.registrar(
                data.username(), data.nombre(), data.apellido(), data.email());

        credencialService.crearCredencialLocal(userId, passwordHasher.hashear(data.contrasena()));

        return userId;
    }
}