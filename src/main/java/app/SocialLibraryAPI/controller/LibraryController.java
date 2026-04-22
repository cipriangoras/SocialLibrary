package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.UpdateLibraryRequest;
import app.SocialLibraryAPI.dto.response.LibraryEntryDTO;
import app.SocialLibraryAPI.entity.Status;
import app.SocialLibraryAPI.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PutMapping("/book/{bookId}")
    public ResponseEntity<LibraryEntryDTO> updateLibraryStatus(
            @PathVariable Integer bookId,
            @RequestBody UpdateLibraryRequest request,
            Principal principal) {
        log.info("REST request to update library for book id: {} by user: {}", bookId, principal.getName());
        return ResponseEntity.ok(libraryService.updateLibraryEntry(principal.getName(), bookId, request));
    }

    @GetMapping
    public ResponseEntity<Page<LibraryEntryDTO>> getMyLibrary(
            @RequestParam(required = false) Status status,
            @ParameterObject Pageable pageable,
            Principal principal) {
        log.info("REST request to get library for user: {}, status filter: {}", principal.getName(), status);
        return ResponseEntity.ok(libraryService.getUserLibrary(principal.getName(), status, pageable));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<LibraryEntryDTO> getEntryForBook(@PathVariable Integer bookId, Principal principal) {
        log.info("REST request to get library entry for book id: {} by user: {}", bookId, principal.getName());
        return ResponseEntity.ok(libraryService.getLibraryEntryForBook(principal.getName(), bookId));
    }

    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> removeBookFromLibrary(@PathVariable Integer bookId, Principal principal) {
        log.info("REST request to remove book id: {} from library by user: {}", bookId, principal.getName());
        libraryService.removeBookFromLibrary(principal.getName(), bookId);
        return ResponseEntity.ok().build();
    }
}