package app.SocialLibraryAPI.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    boolean existsByIsbn(String isbn);
    java.util.Optional<BookEntity> findByIsbn(String isbn);

    @Query("SELECT DISTINCT b FROM BookEntity b LEFT JOIN b.genres g " +
            "WHERE (LOWER(b.title) LIKE LOWER(:search) OR LOWER(b.author) LIKE LOWER(:search)) " +
            "AND (:genreId IS NULL OR g.id = :genreId)")
    Page<BookEntity> findWithFilters(@Param("search") String search, @Param("genreId") Integer genreId, Pageable pageable);

    @Query("SELECT lib.book FROM UserBookLibraryEntity lib " +
            "WHERE lib.user.id IN :followingIds " +
            "AND lib.book.id NOT IN :excludedBookIds " +
            "GROUP BY lib.book " +
            "ORDER BY COUNT(lib.book) DESC, lib.book.rating DESC")
    List<BookEntity> findRecommendedBooksFromFollowing(
            @Param("followingIds") List<Long> followingIds,
            @Param("excludedBookIds") List<Integer> excludedBookIds,
            Pageable pageable);


    @Query("SELECT DISTINCT b FROM BookEntity b JOIN b.genres g " +
            "WHERE g.id IN :genreIds " +
            "AND b.id NOT IN :excludedBookIds " +
            "ORDER BY b.rating DESC")
    List<BookEntity> findRecommendedBooksByGenres(
            @Param("genreIds") List<Integer> genreIds,
            @Param("excludedBookIds") List<Integer> excludedBookIds,
            Pageable pageable);


    @Query("SELECT b FROM BookEntity b " +
            "WHERE b.id NOT IN :excludedBookIds " +
            "ORDER BY b.rating DESC")
    List<BookEntity> findTrendingBooks(
            @Param("excludedBookIds") List<Integer> excludedBookIds,
            Pageable pageable);

    Optional<BookEntity> findFirstByTitleIgnoreCase(String title);
}
