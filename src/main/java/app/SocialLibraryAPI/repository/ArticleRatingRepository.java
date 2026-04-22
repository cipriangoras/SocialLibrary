package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ArticleRating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArticleRatingRepository extends JpaRepository<ArticleRating, Integer> {
    Optional<ArticleRating> findByArticle_IdAndUser_Email(Integer articleId, String email);
}