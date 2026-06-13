package app.SocialLibraryAPI.library;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBookLibraryRepository extends JpaRepository<UserBookLibraryEntity, Integer> {

    Optional<UserBookLibraryEntity> findByBook_IdAndUser_Email(Integer bookId, String email);

    Page<UserBookLibraryEntity> findByUser_Email(String email, Pageable pageable);

    Page<UserBookLibraryEntity> findByUser_EmailAndStatus(String email, Status status, Pageable pageable);

    Page<UserBookLibraryEntity> findByUser_Id(Long userId, Pageable pageable);
    Page<UserBookLibraryEntity> findByUser_IdAndStatus(Long userId, Status status, Pageable pageable);
    @Query("SELECT lib FROM UserBookLibraryEntity lib " +
            "JOIN FETCH lib.book " +
            "JOIN FETCH lib.user " +
            "WHERE lib.user.id IN :userIds " +
            "AND lib.updatedAt < :cursor " +
            "ORDER BY lib.updatedAt DESC")
    List<UserBookLibraryEntity> findByUserIdsWithCursor(
            @Param("userIds") List<Long> userIds,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable);
}
