package app.SocialLibraryAPI.recommendation;

import app.SocialLibraryAPI.article.dto.ArticleDTO;
import app.SocialLibraryAPI.book.dto.BookDTO;
import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getBookRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "10", required = false) int limit) {
        log.info("REST request to fetch up to {} book recommendations for user: {}", limit, principal.getName());
        return ResponseEntity.status(200).body(recommendationService.getBookRecommendations(principal.getName(), limit));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUserRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "5", required = false) int limit) {
        log.info("REST request to fetch up to {} user recommendations for user: {}", limit, principal.getName());
        return ResponseEntity.status(200).body(recommendationService.getUserRecommendations(principal.getName(), limit));
    }

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleDTO>> getArticleRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "3", required = false) int limit) {
        log.info("REST request to fetch up to {} article recommendations for user: {}", limit, principal.getName());
        return ResponseEntity.status(200).body(recommendationService.getArticleRecommendations(principal.getName(), limit));
    }

    @GetMapping("/clubs")
    public ResponseEntity<List<BookClubDTO>> getClubRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "3", required = false) int limit) {
        log.info("REST request to fetch up to {} club recommendations for user: {}", limit, principal.getName());
        return ResponseEntity.status(200).body(recommendationService.getClubRecommendations(principal.getName(), limit));
    }
}