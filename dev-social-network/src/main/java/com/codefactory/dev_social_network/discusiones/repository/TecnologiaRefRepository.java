package com.codefactory.dev_social_network.discusiones.repository;

import com.codefactory.dev_social_network.discusiones.entity.TecnologiaRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TecnologiaRefRepository extends JpaRepository<TecnologiaRef, UUID> {
}
