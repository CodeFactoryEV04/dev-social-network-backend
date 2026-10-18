package main.java.com.codefactory.dev_social_network.Mensajeria.interfaces;

import com.codefactory.dev_social_network.Mensajeria.dto.SendMessageRequest;
import com.codefactory.dev_social_network.Mensajeria.dto.MessageResponse;
import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(UUID senderId, SendMessageRequest request);
}
