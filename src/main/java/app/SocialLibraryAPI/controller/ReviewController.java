package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.CreateReviewRequest;
import app.SocialLibraryAPI.dto.response.ReviewDTO;
import app.SocialLibraryAPI.service.ReviewService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody CreateReviewRequest request, Principal principal) {
        log.info("REST request to add review for book id: {} by user: {}", request.bookId(), principal.getName());
        return ResponseEntity.status(201).body(reviewService.addReview(principal.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody CreateReviewRequest request,
            Principal principal) {
        log.info("REST request to update review id: {} by user: {}", id, principal.getName());
        return ResponseEntity.ok(reviewService.updateReview(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id, Principal principal) {
        log.info("REST request to delete review id: {} by user: {}", id, principal.getName());
        reviewService.deleteReview(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsForBook(
            @PathVariable Integer bookId,
            @ParameterObject Pageable pageable) {
        log.info("REST request to fetch reviews for book id: {} with pagination", bookId);
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId, pageable));
    }
}