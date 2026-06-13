package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.club.dto.CreateBookClubRequest;
import app.SocialLibraryAPI.club.dto.CreateClubSessionRequest;
import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.club.dto.ClubSessionDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<BookClubDTO> updateClub(
            @PathVariable Integer id,
            @Valid @RequestBody CreateBookClubRequest request,
            Principal principal) {
        log.info("REST request to update book club id: {} by user: {}", id, principal.getName());
        return ResponseEntity.status(200).body(bookClubService.updateBookClub(principal.getName(), id, request));
    }

    @PostMapping("/{clubId}/join")
    public ResponseEntity<Void> joinClub(@PathVariable Integer clubId, Principal principal) {
        log.info("REST request to join club id: {} by user: {}", clubId, principal.getName());
        bookClubService.joinClub(principal.getName(), clubId);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/{clubId}/leave")
    public ResponseEntity<Void> leaveClub(@PathVariable Integer clubId, Principal principal) {
        log.info("REST request to leave club id: {} by user: {}", clubId, principal.getName());
        bookClubService.leaveClub(principal.getName(), clubId);
        return ResponseEntity.status(200).build();
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
            @RequestParam(required = false) String search,
            @ParameterObject Pageable pageable) {
        log.info("REST request to get all book clubs filtered by search: {}", search);
        return ResponseEntity.status(200).body(bookClubService.getAllBookClubs(search, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer id, Principal principal) {
        log.info("REST request to delete book club id: {} by user: {}", id, principal.getName());
        bookClubService.deleteBookClub(principal.getName(), id);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookClubDTO> getClubById(@PathVariable Integer id) {
        log.info("REST request to fetch book club by id: {}", id);
        return ResponseEntity.status(200).body(bookClubService.getBookClubById(id));
    }

    @PutMapping("/{clubId}/current-book")
    public ResponseEntity<BookClubDTO> changeCurrentBook(
            @PathVariable Integer clubId,
            @RequestParam String newBookTitle,
            Principal principal) {
        log.info("REST request to change current book to '{}' for club id: {} by user: {}", newBookTitle, clubId, principal.getName());
        return ResponseEntity.status(200).body(bookClubService.changeCurrentBook(principal.getName(), clubId, newBookTitle));
    }

    @GetMapping("/{clubId}/sessions")
    public ResponseEntity<List<ClubSessionDTO>> getClubSessions(@PathVariable Integer clubId) {
        log.info("REST request to get sessions for club id: {}", clubId);
        return ResponseEntity.status(200).body(bookClubService.getClubSessions(clubId));
    }

    @GetMapping("/{clubId}/sessions/{sessionId}")
    public ResponseEntity<ClubSessionDTO> getSessionById(@PathVariable Integer clubId, @PathVariable Integer sessionId) {
        log.info("REST request to get session by id: {}", sessionId);
        return ResponseEntity.status(200).body(bookClubService.getSessionById(clubId, sessionId));
    }

    @PutMapping("/{clubId}/sessions/{sessionId}/close")
    public ResponseEntity<ClubSessionDTO> closeSession(
            @PathVariable Integer clubId,
            @PathVariable Integer sessionId,
            Principal principal) {
        log.info("REST request to close session id: {} in club id: {} by user: {}", sessionId, clubId, principal.getName());

        ClubSessionDTO closedSession = bookClubService.closeSession(principal.getName(), clubId, sessionId);
        return ResponseEntity.status(200).body(closedSession);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookClubDTO>> getUserClubs(@PathVariable Long userId) {
        log.info("REST request to fetch book clubs for user id: {}", userId);
        return ResponseEntity.status(200).body(bookClubService.getUserClubs(userId));
    }

}