package com.codefactory.dev_social_network.Mensajeria.repository;

import com.codefactory.dev_social_network.Mensajeria.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository
        extends JpaRepository<MessageEntity, UUID> {
}