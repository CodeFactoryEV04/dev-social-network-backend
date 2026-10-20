package com.codefactory.dev_social_network.usuarios.repository;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.UserRegistrationPort;

@Component
public class UserRegistrationAdapter implements UserRegistrationPort {

    private final UsuarioJpaRepository jpaRepository;

    public UserRegistrationAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UUID registrar(String username, String nombre, String apellido, String email) {
        Usuario guardado = jpaRepository.save(new Usuario(username, nombre, apellido, email));
        return guardado.getId();
    }
}