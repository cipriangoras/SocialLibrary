package app.SocialLibraryAPI.club;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubSessionRepository extends JpaRepository<ClubSessionEntity, Integer> {
    List<ClubSessionEntity> findByBookClub_IdOrderByStartTimeAsc(Integer clubId);
}