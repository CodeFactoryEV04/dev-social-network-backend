package com.codefactory.dev_social_network.mensajeria.interfaces;

import java.util.UUID;

public interface AuthenticatedUserProvider {

    UUID currentUserId();
}