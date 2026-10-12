package com.codefactory.dev_social_network.usuarios.interfaces;

import com.codefactory.dev_social_network.usuarios.dto.RegistrationData;

public interface RegistrationRule {

    void validate(RegistrationData data);
}