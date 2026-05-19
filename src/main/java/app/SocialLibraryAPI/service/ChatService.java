package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.SendChatMessageRequest;
import app.SocialLibraryAPI.dto.response.ChatMessageDTO;
import app.SocialLibraryAPI.entity.ChatMessage;
import app.SocialLibraryAPI.entity.ClubSession;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.ChatMessageRepository;
import app.SocialLibraryAPI.repository.ClubSessionRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ClubSessionRepository clubSessionRepository;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository, ClubSessionRepository clubSessionRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.clubSessionRepository = clubSessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatMessageDTO saveMessage(String userEmail, Integer sessionId, SendChatMessageRequest request) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ClubSession session = clubSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        ChatMessage message = new ChatMessage();
        message.setContent(request.content());
        message.setSentAt(LocalDateTime.now());
        message.setUser(user);
        message.setSession(session);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return new ChatMessageDTO(
                savedMessage.getId(),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                user.getId(),
                user.getFullName(),
                user.getProfilePicUrl(),
                session.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getSessionHistory(Integer sessionId) {
        return chatMessageRepository.findBySession_IdOrderBySentAtAsc(sessionId).stream()
                .map(msg -> new ChatMessageDTO(
                        msg.getId(),
                        msg.getContent(),
                        msg.getSentAt(),
                        msg.getUser().getId(),
                        msg.getUser().getFullName(),
                        msg.getUser().getProfilePicUrl(),
                        msg.getSession().getId()
                )).collect(Collectors.toList());
    }
}