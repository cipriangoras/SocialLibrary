package app.SocialLibraryAPI.article;

import app.SocialLibraryAPI.article.dto.CreateArticleRequest;
import app.SocialLibraryAPI.article.dto.ArticleDTO;
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

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
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
        return ResponseEntity.status(200).body(articleService.getArticlesFeed(pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Integer id) {
        log.info("REST request to get article id: {}", id);
        return ResponseEntity.status(200).body(articleService.getArticleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Integer id,
            @Valid @RequestBody CreateArticleRequest request,
            Principal principal) {
        log.info("REST request to update article id: {} by user: {}", id, principal.getName());
        return ResponseEntity.status(200).body(articleService.updateArticle(principal.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Integer id, Principal principal) {
        log.info("REST request to delete article id: {} by user: {}", id, principal.getName());
        articleService.deleteArticle(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<ArticleDTO>> getArticlesByAuthor(
            @PathVariable Long authorId,
            @ParameterObject Pageable pageable) {

        log.info("REST request to get articles by author id: {}", authorId);
        return ResponseEntity.ok(articleService.getArticlesByAuthorId(authorId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> searchArticles(
            @RequestParam(required = false) String search,
            @ParameterObject Pageable pageable){
        log.info("REST request to search articles by: {}", search);
        return ResponseEntity.ok(articleService.searchArticles(search, pageable));
    }

}