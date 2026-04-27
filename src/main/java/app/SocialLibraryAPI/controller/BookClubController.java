package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.CreateBookClubRequest;
import app.SocialLibraryAPI.dto.request.CreateClubSessionRequest;
import app.SocialLibraryAPI.dto.response.BookClubDTO;
import app.SocialLibraryAPI.dto.response.ClubSessionDTO;
import app.SocialLibraryAPI.service.BookClubService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/book-clubs")
public class BookClubController {

    private final BookClubService bookClubService;

    public BookClubController(BookClubService bookClubService) {
        this.bookClubService = bookClubService;
    }

    @PostMapping
    public ResponseEntity<BookClubDTO> createClub(
            @Valid @RequestBody CreateBookClubRequest request,
            Principal principal) {
        log.info("REST request to create book club by user: {}", principal.getName());
        return ResponseEntity.status(201).body(bookClubService.createClub(principal.getName(), request));
    }

    @PostMapping("/{clubId}/join")
    public ResponseEntity<Void> joinClub(@PathVariable Integer clubId, Principal principal) {
        log.info("REST request to join club id: {} by user: {}", clubId, principal.getName());
        bookClubService.joinClub(principal.getName(), clubId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{clubId}/leave")
    public ResponseEntity<Void> leaveClub(@PathVariable Integer clubId, Principal principal) {
        log.info("REST request to leave club id: {} by user: {}", clubId, principal.getName());
        bookClubService.leaveClub(principal.getName(), clubId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clubId}/sessions")
    public ResponseEntity<ClubSessionDTO> addSession(
            @PathVariable Integer clubId,
            @Valid @RequestBody CreateClubSessionRequest request,
            Principal principal) {
        log.info("REST request to add session to club id: {} by user: {}", clubId, principal.getName());
        return ResponseEntity.status(201).body(bookClubService.addSession(principal.getName(), clubId, request));
    }

    @GetMapping
    public ResponseEntity<Page<BookClubDTO>> getAllClubs(
            @RequestParam(required = false) String bookTitle,
            @ParameterObject Pageable pageable) {
        log.info("REST request to get all book clubs filtered by book title: {}", bookTitle);
        return ResponseEntity.ok(bookClubService.getAllBookClubs(bookTitle, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer id, Principal principal) {
        log.info("REST request to delete book club id: {} by user: {}", id, principal.getName());
        bookClubService.deleteBookClub(principal.getName(), id);
        return ResponseEntity.ok().build();
    }
}