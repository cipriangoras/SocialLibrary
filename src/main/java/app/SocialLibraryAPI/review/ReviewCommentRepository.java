package app.SocialLibraryAPI.review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Integer> {
    List<ReviewCommentEntity> findByReview_IdOrderByCreatedAtAsc(Integer reviewId);
}