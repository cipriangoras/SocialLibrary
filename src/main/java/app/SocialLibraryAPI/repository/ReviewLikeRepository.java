package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLikeEntity, Integer> {
    Optional<ReviewLikeEntity> findByReview_IdAndUser_Email(Integer reviewId, String email);
}