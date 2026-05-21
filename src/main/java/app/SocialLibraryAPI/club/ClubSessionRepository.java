package app.SocialLibraryAPI.club;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClubSessionRepository extends JpaRepository<ClubSessionEntity, Integer> {
    List<ClubSessionEntity> findByBookClub_IdOrderByStartTimeAsc(Integer clubId);
    long countByBookClub_IdAndIsActiveFalse(Integer clubId);

    List<ClubSessionEntity> findByStartTimeBeforeOrderByStartTimeDesc(LocalDateTime cursor, Pageable pageable);

    List<ClubSessionEntity> findByBookClub_User_IdInAndStartTimeBeforeOrderByStartTimeDesc(
            List<Long> userIds, LocalDateTime cursor, Pageable pageable);
}