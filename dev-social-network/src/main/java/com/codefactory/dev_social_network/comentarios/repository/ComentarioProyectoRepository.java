package com.codefactory.dev_social_network.comentarios.repository;
 
import com.codefactory.dev_social_network.comentarios.entity.ComentarioProyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioProyectoRepository extends JpaRepository<ComentarioProyecto, UUID> {

    List<ComentarioProyecto> findByIdProyectoOrderByFechaCreacionAsc(UUID idProyecto);
    
}