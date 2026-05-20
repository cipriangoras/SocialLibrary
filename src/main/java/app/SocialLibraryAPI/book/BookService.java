package app.SocialLibraryAPI.book;

import app.SocialLibraryAPI.book.dto.CreateBookRequest;
import app.SocialLibraryAPI.book.dto.BookDTO;
import app.SocialLibraryAPI.genre.GenreEntity;
import app.SocialLibraryAPI.genre.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Autowired
    private BookService (BookRepository bookRepository, GenreRepository genreRepository){
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
    }

    public BookDTO createBook(CreateBookRequest request) {
        log.info("Attempting to create a new book: {}", request.title());

        if (bookRepository.existsByIsbn(request.isbn())) {
            log.error("Failed to create book. ISBN already exists: {}", request.isbn());
            throw new IllegalStateException("ISBN already in use: " + request.isbn());
        }

        BookEntity book = new BookEntity();
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
        book.setPublicationYear(request.publicationYear());
        book.setCoverImageUrl(request.coverImageUrl());


        if (request.genreIds() != null && !request.genreIds().isEmpty()) {
            List<GenreEntity> selectedGenresList = genreRepository.findAllById(request.genreIds());

            Set<GenreEntity> selectedGenres = new HashSet<>(selectedGenresList);
            book.setGenres(selectedGenres);
        } else {
            book.setGenres(new HashSet<>());
        }

        BookEntity savedBook = bookRepository.save(book);

        log.info("Successfully created book '{}' with ID: {}", savedBook.getTitle(), savedBook.getId());
        return BookMapper.toDTO(savedBook);
    }

    public Page<BookDTO> getAllBooks(String search, Integer genreId, Pageable pageable) {
        log.debug("Fetching books with search='{}', genreId={}, pageable={}", search, genreId, pageable);


        String searchParam = (search == null || search.trim().isEmpty())
                ? "%"
                : "%" + search.trim() + "%";

        var bookPage = bookRepository.findWithFilters(searchParam, genreId, pageable);

        log.info("Found {} books matching the filters", bookPage.getTotalElements());
        return bookPage.map(BookMapper::toDTO);
    }

    public BookDTO getBookById(Integer id) {
        log.info("Fetching book with id: {}", id);
        return bookRepository.findById(id)
                .map(BookMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    public BookDTO updateBook(Integer bookId, @Valid CreateBookRequest request) {
        log.info("Attempting to update book with id: {}", bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Failed to update. Book not found with id: {}", bookId);
                    return new EntityNotFoundException("Book not found with id: " + bookId);
                });

        bookRepository.findByIsbn(request.isbn()).ifPresent(existing -> {
            if (!existing.getId().equals(bookId)) {
                log.error("Failed to update. ISBN {} is already used by book id: {}", request.isbn(), existing.getId());
                throw new IllegalStateException("ISBN already in use by another book: " + request.isbn());
            }
        });

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
        book.setPublicationYear(request.publicationYear());
        book.setCoverImageUrl(request.coverImageUrl());

        if (request.genreIds() != null && !request.genreIds().isEmpty()) {
            List<GenreEntity> selectedGenresList = genreRepository.findAllById(request.genreIds());
            book.setGenres(new HashSet<>(selectedGenresList));
        } else {
            book.setGenres(new HashSet<>());
        }

        BookEntity updatedBook = bookRepository.save(book);
        log.info("Successfully updated book with id: {}", updatedBook.getId());

        return BookMapper.toDTO(updatedBook);
    }

    public void deleteBookById(Integer bookId) {
        log.info("Attempting to delete book with id: {}", bookId);

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Failed to delete. Book not found with id: {}", bookId);
                    return new EntityNotFoundException("Book not found with id: " + bookId);
                });

        bookRepository.delete(book);
        log.info("Successfully deleted book with id: {}", bookId);
    }

}
