package app.SocialLibraryAPI.controller;


import app.SocialLibraryAPI.dto.request.CreateBookRequest;
import app.SocialLibraryAPI.dto.response.BookDTO;
import app.SocialLibraryAPI.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/management/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(
            @RequestBody @Valid CreateBookRequest bookToCreate) {
        log.info("REST request to create book: {}", bookToCreate.title());
        BookDTO createdBook = bookService.createBook(bookToCreate);
        return ResponseEntity.status(201).body(createdBook);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer genreId,
            @ParameterObject Pageable pageable
    ) {
        log.info("REST request to fetch all books");
        Page<BookDTO> books = bookService.getAllBooks(search, genreId, pageable);
        return ResponseEntity.status(200).body(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Integer id,
            @RequestBody @Valid CreateBookRequest bookRequest) {

        log.info("REST request to update book with id: {}", id);
        BookDTO updatedBook = bookService.updateBook(id, bookRequest);
        return ResponseEntity.status(200).body(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Integer id) {
        log.info("REST request to delete book with id: {}", id);
        bookService.deleteBookById(id);
        return ResponseEntity.status(200).build();
    }

}
