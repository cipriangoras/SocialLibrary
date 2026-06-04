package app.SocialLibraryAPI.library;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBookLibraryRepository extends JpaRepository<UserBookLibraryEntity, Integer> {

    Optional<UserBookLibraryEntity> findByBook_IdAndUser_Email(Integer bookId, String email);

    Page<UserBookLibraryEntity> findByUser_Email(String email, Pageable pageable);

    Page<UserBookLibraryEntity> findByUser_EmailAndStatus(String email, Status status, Pageable pageable);

    Page<UserBookLibraryEntity> findByUser_Id(Long userId, Pageable pageable);
    Page<UserBookLibraryEntity> findByUser_IdAndStatus(Long userId, Status status, Pageable pageable);
    List<UserBookLibraryEntity> findByUser_IdInAndUpdatedAtLessThanOrderByUpdatedAtDesc(
            List<Long> userIds, LocalDateTime cursor, Pageable pageable);
}
