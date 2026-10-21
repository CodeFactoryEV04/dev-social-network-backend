package com.codefactory.dev_social_network.usuarios.service;

import org.springframework.stereotype.Component;

import com.codefactory.dev_social_network.shared.exception.PasswordInseguraException;

@Component
public class PasswordValidator {

    public void validar(String contrasena) {
        if (contrasena.length() < 8) {
            throw new PasswordInseguraException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!contrasena.chars().anyMatch(Character::isUpperCase)) {
            throw new PasswordInseguraException("La contraseña debe tener al menos 1 letra mayúscula.");
        }
        if (!contrasena.chars().anyMatch(Character::isDigit)) {
            throw new PasswordInseguraException("La contraseña debe tener al menos 1 número.");
        }
        if (contrasena.chars().allMatch(Character::isLetterOrDigit)) {
            throw new PasswordInseguraException("La contraseña debe tener al menos 1 carácter especial.");
        }
    }
}