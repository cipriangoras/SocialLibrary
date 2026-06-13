package app.SocialLibraryAPI.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
    @Query("SELECT a FROM ArticleEntity a " +
            "JOIN FETCH a.author " +
            "LEFT JOIN FETCH a.relatedBook " +
            "ORDER BY a.createdAt DESC")
    Page<ArticleEntity> findAllWithDetailsOrderByCreatedAtDesc(Pageable pageable);
    List<ArticleEntity> findByAuthor_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
            List<Long> authorIds,
            LocalDateTime cursor,
            Pageable pageable
    );

    Page<ArticleEntity> findByAuthor_Id(Long authorId, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a " +
            "WHERE a.relatedBook.id IN :userBookIds " +
            "ORDER BY a.createdAt DESC")
    List<ArticleEntity> findArticlesRelatedToUserBooks(
            @Param("userBookIds") List<Integer> userBookIds,
            Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a " +
            "ORDER BY size(a.likes) DESC, size(a.comments) DESC")
    List<ArticleEntity> findTrendingArticles(Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a LEFT JOIN a.relatedBook b LEFT JOIN a.author u WHERE " +
            "(LOWER(a.title) LIKE LOWER(:search) " +
            "OR LOWER(u.fullName) LIKE LOWER(:search) " +
            "OR LOWER(b.title) LIKE LOWER(:search))")
    Page<ArticleEntity> findWithFilters(@Param("search") String search, Pageable pageable);
}