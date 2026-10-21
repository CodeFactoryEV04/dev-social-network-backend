package com.codefactory.dev_social_network.comentarios.repository;

import com.codefactory.dev_social_network.comentarios.entity.UsuarioRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRefRepository extends JpaRepository<UsuarioRef, UUID> {
}

