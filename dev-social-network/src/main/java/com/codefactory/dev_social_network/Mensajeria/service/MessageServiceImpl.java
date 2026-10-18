package com.codefactory.dev_social_network.Mensajeria.service;

import com.codefactory.dev_social_network.mensajeria.dto.MessageResponse;
import com.codefactory.dev_social_network.mensajeria.dto.SendMessageRequest;
import com.codefactory.dev_social_network.mensajeria.entity.ConversationEntity;
import com.codefactory.dev_social_network.mensajeria.entity.MessageEntity;
import com.codefactory.dev_social_network.shared.exception.ConversationAccessDeniedException;
import com.codefactory.dev_social_network.shared.exception.ConversationNotFoundException;
import com.codefactory.dev_social_network.shared.exception.RecipientNotFoundException;
import com.codefactory.dev_social_network.shared.exception.SelfMessageException;
import com.codefactory.dev_social_network.mensajeria.interfaces.MessageService;
import com.codefactory.dev_social_network.mensajeria.interfaces.UserDirectory;
import com.codefactory.dev_social_network.mensajeria.repository.ConversationRepository;
import com.codefactory.dev_social_network.mensajeria.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final UserDirectory userDirectory;
    private final ConversationResolver conversationResolver;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final Clock clock;

    public MessageServiceImpl(UserDirectory userDirectory,
                              ConversationResolver conversationResolver,
                              ConversationRepository conversationRepository,
                              MessageRepository messageRepository,
                              MessageMapper messageMapper,
                              Clock clock) {
        this.userDirectory = userDirectory;
        this.conversationResolver = conversationResolver;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID senderId, SendMessageRequest request) {
        UUID receiverId = userDirectory.findUserIdByUsername(request.receiverUsername())
                .orElseThrow(RecipientNotFoundException::new);

        if (senderId.equals(receiverId)) {
            throw new SelfMessageException();
        }

        ConversationEntity conversation = conversationResolver.resolve(senderId, receiverId);

        MessageEntity message = new MessageEntity();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setContent(request.content());
        message.setSentAt(Instant.now(clock));

        return messageMapper.toResponse(messageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessages(UUID requesterId, UUID conversationId) {
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(ConversationNotFoundException::new);

        boolean isParticipant = requesterId.equals(conversation.getParticipantOneId())
                || requesterId.equals(conversation.getParticipantTwoId());
        if (!isParticipant) {
            throw new ConversationAccessDeniedException();
        }

        return messageRepository.findByConversation_IdOrderBySentAtAsc(conversationId).stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}