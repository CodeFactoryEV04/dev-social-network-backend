package main.java.com.codefactory.dev_social_network.Mensajeria.repository;

import main.java.com.codefactory.dev_social_network.Mensajeria.entity.ConversationEntity;
import org.springframework.data.repository.Repository;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends Repository<ConversationEntity, UUID>  {
    
    Optional<ConversationEntity> findByParticipantOneIdAndParticipantTwoId(UUID participantOneId, UUID participantTwoId);
    ConversationEntity save(ConversationEntity conversation);
}
