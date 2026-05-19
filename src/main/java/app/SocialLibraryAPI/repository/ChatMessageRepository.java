package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findBySession_IdOrderBySentAtAsc(Integer sessionId);
}