package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ClubSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubSessionRepository extends JpaRepository<ClubSession, Integer> {
    List<ClubSession> findByBookClub_IdOrderByStartTimeAsc(Integer clubId);
}