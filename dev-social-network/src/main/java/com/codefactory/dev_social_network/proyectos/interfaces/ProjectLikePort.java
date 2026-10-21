package com.codefactory.dev_social_network.proyectos.interfaces;

import com.codefactory.dev_social_network.proyectos.dto.LikedProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectLikePort {

    /**
     * 
     *
     * @return 
     *         
     */
    boolean registrarLike(Long projectId, UUID usuarioId);

    long contarLikes(Long projectId);

    List<LikedProjectResponse> listarPorUsuario(UUID usuarioId);
}
