package com.codefactory.dev_social_network.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.dev_social_network.autenticacion.interfaces.CredencialService;
import com.codefactory.dev_social_network.shared.exception.EmailDuplicadoException;
import com.codefactory.dev_social_network.shared.exception.FormatoEmailInvalidoException;
import com.codefactory.dev_social_network.shared.exception.PasswordInseguraException;
import com.codefactory.dev_social_network.usuarios.entity.UserProfileEntity;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.PasswordHasher;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrarUsuarioUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.RegistrationRule;
import com.codefactory.dev_social_network.usuarios.interfaces.UserRegistrationPort;

@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private static final Pattern EMAIL_REGEX =
        Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final CredencialService credencialService;
    private final UserProfileRepositoryPort userProfileRepositoryPort;
    private final PasswordHasher passwordHasher;
    private final List<RegistrationRule> rules;

    public RegistrarUsuarioUseCaseImpl(UserRegistrationPort userRegistrationPort,
                                       CredencialService credencialService,
                                       UserProfileRepositoryPort userProfileRepositoryPort,
                                       PasswordHasher passwordHasher) {
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

        if (usuarioRepositoryPort.existePorEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        validarSeguridadContraseña(contraseña);

        Usuario usuarioGuardado = usuarioRepositoryPort.guardar(
                new Usuario(nombre, apellido, email));
        credencialService.crearCredencialLocal(
                usuarioGuardado.getId(), passwordHasher.hashear(contraseña));

        credencialService.crearCredencialLocal(userId, passwordHasher.hashear(data.contrasena()));

        return userId;
    }
}