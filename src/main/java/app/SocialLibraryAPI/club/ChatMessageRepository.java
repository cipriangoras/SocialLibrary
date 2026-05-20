package app.SocialLibraryAPI.club;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findBySession_IdOrderBySentAtAsc(Integer sessionId);
}