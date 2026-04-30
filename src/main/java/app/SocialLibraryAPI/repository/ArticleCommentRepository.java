package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ArticleCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleCommentEntity, Integer> {
    List<ArticleCommentEntity> findByArticle_IdOrderByCreatedAtAsc(Integer articleId);
}