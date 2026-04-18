package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.Status;
import app.SocialLibraryAPI.entity.UserBookLibraryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBookLibraryRepository extends JpaRepository<UserBookLibraryEntity, Integer> {

    Optional<UserBookLibraryEntity> findByBook_IdAndUser_Email(Integer bookId, String email);

    Page<UserBookLibraryEntity> findByUser_Email(String email, Pageable pageable);

    Page<UserBookLibraryEntity> findByUser_EmailAndStatus(String email, Status status, Pageable pageable);
}