package com.codefactory.dev_social_network.mensajeria.repository;

import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConversationQueryRepository extends Repository<ConversationEntity, UUID> {

    interface UnreadCountView {
        UUID getConversationId();

        Long getUnreadCount();
    }

    @Query("""
            select c from ConversationEntity c
            where c.participantOneId = :userId or c.participantTwoId = :userId
            order by c.createdAt desc
            """)
    List<ConversationEntity> findAllByParticipant(@Param("userId") UUID userId);

   
    @Query("""
            select m.conversation.id as conversationId, count(m) as unreadCount
            from MessageEntity m
            where m.senderId <> :userId
              and (m.conversation.participantOneId = :userId or m.conversation.participantTwoId = :userId)
              and (
                    not exists (select r.id from ConversationReadStateEntity r
                                where r.conversation.id = m.conversation.id
                                  and r.userId = :userId)
                 or m.sentAt > (select r2.lastReadAt from ConversationReadStateEntity r2
                                where r2.conversation.id = m.conversation.id
                                  and r2.userId = :userId)
              )
            group by m.conversation.id
            """)
    List<UnreadCountView> countUnreadByConversation(@Param("userId") UUID userId);
}