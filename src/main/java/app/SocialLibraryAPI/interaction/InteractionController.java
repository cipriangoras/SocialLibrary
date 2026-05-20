package app.SocialLibraryAPI.interaction;

import app.SocialLibraryAPI.interaction.dto.CommentRequest;
import app.SocialLibraryAPI.interaction.dto.CommentResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/articles/{id}/like")
    public ResponseEntity<Void> toggleArticleLike(@PathVariable Integer id, Principal principal) {
        interactionService.toggleArticleLike(principal.getName(), id);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addArticleComment(
            @PathVariable Integer id,
            @Valid @RequestBody CommentRequest request,
            Principal principal) {
        return ResponseEntity.status(201).body(interactionService.addArticleComment(principal.getName(), id, request));
    }

    @GetMapping("/articles/{id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getArticleComments(@PathVariable Integer id) {
        return ResponseEntity.ok(interactionService.getArticleComments(id));
    }

    @PostMapping("/reviews/{id}/like")
    public ResponseEntity<Void> toggleReviewLike(@PathVariable Integer id, Principal principal) {
        interactionService.toggleReviewLike(principal.getName(), id);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/reviews/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addReviewComment(
            @PathVariable Integer id,
            @Valid @RequestBody CommentRequest request,
            Principal principal) {
        return ResponseEntity.status(201).body(interactionService.addReviewComment(principal.getName(), id, request));
    }

    @GetMapping("/reviews/{id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getReviewComments(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(interactionService.getReviewComments(id));
    }
}