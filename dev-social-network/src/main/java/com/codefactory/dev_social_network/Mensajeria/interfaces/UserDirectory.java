package com.codefactory.dev_social_network.Mensajeria.interfaces;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto: lo que mensajería necesita saber del módulo de usuarios.
 * Lo implementa un adaptador que llama a la API pública de usuarios.
 */
public interface UserDirectory {

    Optional<UUID> findUserIdByUsername(String username);
}