package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
    Page<ArticleEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<ArticleEntity> findByAuthor_IdInOrderByCreatedAtDesc(List<Long> authorIds);
    List<ArticleEntity> findByAuthor_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
            List<Long> authorIds,
            LocalDateTime cursor,
            Pageable pageable
    );

    Page<ArticleEntity> findByAuthor_Id(Long authorId, Pageable pageable);

}