package com.codefactory.dev_social_network.mensajeria.interfaces;

import java.util.Optional;
import java.util.UUID;

public interface UserDirectory {

    Optional<UUID> findUserIdByUsername(String username);
}
