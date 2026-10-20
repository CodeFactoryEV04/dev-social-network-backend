package com.codefactory.dev_social_network.proyectos.interfaces;

import com.codefactory.dev_social_network.proyectos.dto.ProjectSummary;

import java.util.Optional;

public interface ProjectFinderPort {

    /**
     * 
     *
     * @return 
     */
    Optional<ProjectSummary> buscarResumenPorId(Long projectId);
}
