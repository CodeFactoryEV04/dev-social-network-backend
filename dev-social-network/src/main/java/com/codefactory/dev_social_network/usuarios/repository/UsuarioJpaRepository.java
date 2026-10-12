package com.codefactory.dev_social_network.usuarios.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codefactory.dev_social_network.usuarios.entity.Usuario;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsername(String username);

}