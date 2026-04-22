package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.CreateArticleRequest;
import app.SocialLibraryAPI.dto.request.RateArticleRequest;
import app.SocialLibraryAPI.dto.response.ArticleDTO;
import app.SocialLibraryAPI.service.ArticleRatingService;
import app.SocialLibraryAPI.service.ArticleService;
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
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleRatingService ratingService;

    public ArticleController(ArticleService articleService, ArticleRatingService ratingService) {
        this.articleService = articleService;
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(
            @Valid @RequestBody CreateArticleRequest request,
            Principal principal) {
        log.info("REST request to create article by user: {}", principal.getName());
        return ResponseEntity.status(201).body(articleService.createArticle(principal.getName(), request));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<ArticleDTO>> getArticleFeed(@ParameterObject Pageable pageable) {
        log.info("REST request to fetch article feed");
        return ResponseEntity.ok(articleService.getArticlesFeed(pageable));
    }

    @PutMapping("/{articleId}/rate")
    public ResponseEntity<Void> rateArticle(
            @PathVariable Integer articleId,
            @Valid @RequestBody RateArticleRequest request,
            Principal principal) {
        log.info("REST request to rate article id: {} by user: {}", articleId, principal.getName());
        ratingService.rateArticle(principal.getName(), articleId, request.score());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Integer id) {
        log.info("REST request to get article id: {}", id);
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Integer id,
            @Valid @RequestBody CreateArticleRequest request,
            Principal principal) {
        log.info("REST request to update article id: {} by user: {}", id, principal.getName());
        return ResponseEntity.ok(articleService.updateArticle(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Integer id, Principal principal) {
        log.info("REST request to delete article id: {} by user: {}", id, principal.getName());
        articleService.deleteArticle(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}