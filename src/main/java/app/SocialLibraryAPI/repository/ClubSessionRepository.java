package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ClubSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubSessionRepository extends JpaRepository<ClubSession, Integer> {
}