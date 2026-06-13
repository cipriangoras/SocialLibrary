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

    @Query("SELECT DISTINCT c FROM BookClubEntity c " +
            "JOIN FETCH c.book " +
            "LEFT JOIN FETCH c.members m " +
            "LEFT JOIN FETCH m.user " +
            "WHERE m.user.id IN :myFollowingIds " +
            "AND c.id NOT IN :myClubIds " +
            "ORDER BY COUNT(m) DESC")
    List<BookClubEntity> findClubsWithFriends(
            @Param("myFollowingIds") List<Long> myFollowingIds,
            @Param("myClubIds") List<Integer> myClubIds,
            Pageable pageable);

    @Query("SELECT bc FROM BookClubEntity bc LEFT JOIN bc.book b " +
            "WHERE (LOWER(bc.name) LIKE LOWER(:search) " +
            "OR LOWER(b.title) LIKE LOWER(:search))")
    Page<BookClubEntity> searchBookClubs(@Param("search") String search, Pageable pageable);


}