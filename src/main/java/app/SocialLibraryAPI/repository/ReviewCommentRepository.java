package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ReviewCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Integer> {
    List<ReviewCommentEntity> findByReview_IdOrderByCreatedAtAsc(Integer reviewId);
}