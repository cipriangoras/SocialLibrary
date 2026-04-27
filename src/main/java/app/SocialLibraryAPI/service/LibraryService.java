package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.UpdateLibraryRequest;
import app.SocialLibraryAPI.dto.response.LibraryEntryDTO;
import app.SocialLibraryAPI.entity.BookEntity;
import app.SocialLibraryAPI.entity.Status;
import app.SocialLibraryAPI.entity.UserBookLibraryEntity;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.BookRepository;
import app.SocialLibraryAPI.repository.UserBookLibraryRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LibraryService {

    private final UserBookLibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LibraryService(UserBookLibraryRepository libraryRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LibraryEntryDTO updateLibraryEntry(String userEmail, Integer bookId, UpdateLibraryRequest request) {
        log.info("Attempting to update library entry for book id: {} by user: {}", bookId, userEmail);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to update library entry. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        UserBookLibraryEntity entry = libraryRepository.findByBook_IdAndUser_Email(bookId, userEmail)
                .orElseGet(() -> {
                    BookEntity book = bookRepository.findById(bookId)
                            .orElseThrow(() -> {
                                log.error("Failed to update library entry. Book not found with id: {}", bookId);
                                return new EntityNotFoundException("Book not found with id: " + bookId);
                            });
                    UserBookLibraryEntity newEntry = new UserBookLibraryEntity();
                    newEntry.setBook(book);
                    newEntry.setUser(user);
                    return newEntry;
                });

        entry.setStatus(request.status());
        entry.setFavorite(request.isFavorite());

        UserBookLibraryEntity saved = libraryRepository.save(entry);
        log.info("Successfully updated library entry id: {} for book id: {}", saved.getId(), bookId);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<LibraryEntryDTO> getUserLibrary(String userEmail, Status status, Pageable pageable) {
        log.info("Fetching library entries for user: {} with status filter: {}", userEmail, status);

        Page<UserBookLibraryEntity> entries;
        if (status != null) {
            entries = libraryRepository.findByUser_EmailAndStatus(userEmail, status, pageable);
        } else {
            entries = libraryRepository.findByUser_Email(userEmail, pageable);
        }

        return entries.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public LibraryEntryDTO getLibraryEntryForBook(String userEmail, Integer bookId) {
        log.info("Fetching library entry for book id: {} by user: {}", bookId, userEmail);

        UserBookLibraryEntity entry = libraryRepository.findByBook_IdAndUser_Email(bookId, userEmail)
                .orElseThrow(() -> {
                    log.error("Library entry not found for book id: {} and user: {}", bookId, userEmail);
                    return new EntityNotFoundException("The book is not in your library");
                });

        return mapToDTO(entry);
    }

    @Transactional
    public void removeBookFromLibrary(String userEmail, Integer bookId) {
        log.info("Attempting to remove book id: {} from library for user: {}", bookId, userEmail);

        UserBookLibraryEntity entry = libraryRepository.findByBook_IdAndUser_Email(bookId, userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to remove. Library entry not found for book id: {} and user: {}", bookId, userEmail);
                    return new EntityNotFoundException("The book was not found in the library.");
                });

        libraryRepository.delete(entry);
        log.info("Successfully removed book id: {} from user library", bookId);
    }

    private LibraryEntryDTO mapToDTO(UserBookLibraryEntity entity) {
        return new LibraryEntryDTO(
                entity.getId(),
                entity.getBook().getId(),
                entity.getBook().getTitle(),
                entity.getStatus(),
                entity.isFavorite()
        );
    }
}