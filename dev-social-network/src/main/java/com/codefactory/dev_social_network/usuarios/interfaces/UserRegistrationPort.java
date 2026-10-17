package com.codefactory.dev_social_network.usuarios.interfaces;

import java.util.UUID;

public interface UserRegistrationPort {

    UUID registrar(String username, String nombre, String apellido, String email);
}