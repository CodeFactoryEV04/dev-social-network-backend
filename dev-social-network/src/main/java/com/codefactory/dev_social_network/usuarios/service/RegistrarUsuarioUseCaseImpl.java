package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.autenticacion.interfaces.CredencialService;
import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;
import com.codefactory.dev_social_network.usuarios.entity.UserProfileEntity;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.PasswordHasher;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;
import com.codefactory.dev_social_network.usuarios.interfaces.UserProfileRepositoryPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UsuarioRepositoryPort;

@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final CredencialService credencialService;
    private final UserProfileRepositoryPort userProfileRepositoryPort;
    private final PasswordHasher passwordHasher;
    private final List<RegistrationRule> rules;

    public RegistrarUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepositoryPort,
            CredencialService credencialService,
            UserProfileRepositoryPort userProfileRepositoryPort,
            PasswordHasher passwordHasher,
            List<RegistrationRule> rules) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.credencialService = credencialService;
        this.userProfileRepositoryPort = userProfileRepositoryPort;
        this.passwordHasher = passwordHasher;
        this.rules = rules;
    }

    @Override
    @Transactional
    public UUID registrar(RegistrationData data) {

        for (RegistrationRule rule : rules) {
            rule.validate(data);
        }

        Usuario usuarioGuardado = usuarioRepositoryPort.guardar(
                new Usuario(data.username(), data.nombre(), data.apellido(), data.email()));

        credencialService.crearCredencialLocal(
                usuarioGuardado.getId(), passwordHasher.hashear(data.contrasena()));

        userProfileRepositoryPort.guardar(new UserProfileEntity(usuarioGuardado));

        return usuarioGuardado.getId();
    }
}