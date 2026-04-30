package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ArticleLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLikeEntity, Integer> {
    Optional<ArticleLikeEntity> findByArticle_IdAndUser_Email(Integer articleId, String email);
}