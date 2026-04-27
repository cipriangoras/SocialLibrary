package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.BookClubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookClubRepository extends JpaRepository<BookClubEntity, Integer> {

    @Query("SELECT bc FROM BookClubEntity bc JOIN bc.book b " +
            "WHERE (:bookTitle IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :bookTitle, '%')))")
    Page<BookClubEntity> findWithFilters(@Param("bookTitle") String bookTitle, Pageable pageable);
}