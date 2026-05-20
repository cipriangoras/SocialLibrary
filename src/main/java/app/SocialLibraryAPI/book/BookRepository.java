package app.SocialLibraryAPI.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    boolean existsByIsbn(String isbn);
    java.util.Optional<BookEntity> findByIsbn(String isbn);

    @Query("SELECT DISTINCT b FROM BookEntity b LEFT JOIN b.genres g " +
            "WHERE (LOWER(b.title) LIKE LOWER(:search) OR LOWER(b.author) LIKE LOWER(:search)) " +
            "AND (:genreId IS NULL OR g.id = :genreId)")
    Page<BookEntity> findWithFilters(@Param("search") String search, @Param("genreId") Integer genreId, Pageable pageable);
}
