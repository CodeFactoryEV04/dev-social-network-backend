package com.codefactory.dev_social_network.usuarios.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.usuarios.dto.UserSummary;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByEmailPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UserFinderByIdPort;

@Component
public class UserFinderAdapter implements UserFinderByEmailPort, UserFinderByIdPort {

    private final UsuarioJpaRepository jpaRepository;

    public UserFinderAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserSummary> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserFinderAdapter::toSummary);
    }

    @Override
    public Optional<UserSummary> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(UserFinderAdapter::toSummary);
    }

    private static UserSummary toSummary(Usuario usuario) {
        return new UserSummary(usuario.getId(), usuario.getNombre(),
                usuario.getApellido(), usuario.getEmail());
    }
}