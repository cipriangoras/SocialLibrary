package app.SocialLibraryAPI.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findBySession_IdOrderBySentAtAsc(Integer sessionId);

    @Query("SELECT COUNT(DISTINCT m.user.id) FROM ChatMessageEntity m WHERE m.session.id = :sessionId")
    long countUniqueParticipantsBySessionId(@Param("sessionId") Integer sessionId);
}