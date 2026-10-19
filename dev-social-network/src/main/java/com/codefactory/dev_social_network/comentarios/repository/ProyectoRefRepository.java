package com.codefactory.dev_social_network.comentarios.repository;
 
import com.codefactory.dev_social_network.comentarios.entity.ProyectoRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProyectoRefRepository extends JpaRepository<ProyectoRef, UUID> {
}