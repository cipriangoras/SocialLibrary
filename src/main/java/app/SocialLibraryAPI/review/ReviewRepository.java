package app.SocialLibraryAPI.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    boolean existsByBook_IdAndUser_Email(Integer bookId, String email);

    Optional<ReviewEntity> findByIdAndUser_Email(Integer id, String email);

    List<ReviewEntity> findByBook_Id(Integer bookId);

    Page<ReviewEntity> findByBook_Id(Integer bookId, Pageable pageable);

    List<ReviewEntity> findByUser_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
            List<Long> userIds,
            LocalDateTime cursor,
            Pageable pageable
    );

    List<ReviewEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);
}