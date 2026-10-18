package main.java.com.codefactory.dev_social_network.Mensajeria.repository;

import com.codefactory.dev_social_network.Mensajeria.entity.MessageEntity;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface MessageRepository extends Repository<MessageEntity, UUID> {
       MessageEntity save(MessageEntity message);
}