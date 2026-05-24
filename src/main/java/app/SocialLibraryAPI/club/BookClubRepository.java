package app.SocialLibraryAPI.club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookClubRepository extends JpaRepository<BookClubEntity, Integer> {

    @Query("SELECT bc FROM BookClubEntity bc JOIN bc.book b " +
            "WHERE (:bookTitle IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :bookTitle, '%')))")
    Page<BookClubEntity> findWithFilters(@Param("bookTitle") String bookTitle, Pageable pageable);

    List<BookClubEntity> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime cursor, Pageable pageable);

    List<BookClubEntity> findByUser_IdInAndCreatedAtBeforeOrderByCreatedAtDesc(
            List<Long> userIds, LocalDateTime cursor, Pageable pageable);

    Page<BookClubEntity> findBookClubEntityByBook_TitleContainingIgnoreCase(String bookTitle, Pageable pageable);
}