package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    boolean existsByIsbn(String isbn);
    java.util.Optional<BookEntity> findByIsbn(String isbn);

    // Caută după titlu/autor SAU după gen. COALESCE sau IS NULL ajută să ignorăm filtrul dacă nu e trimis.
    @Query("SELECT DISTINCT b FROM BookEntity b LEFT JOIN b.genres g " +
            "WHERE (LOWER(b.title) LIKE LOWER(:search) OR LOWER(b.author) LIKE LOWER(:search)) " +
            "AND (:genreId IS NULL OR g.id = :genreId)")
    Page<BookEntity> findWithFilters(@Param("search") String search, @Param("genreId") Integer genreId, Pageable pageable);
}
