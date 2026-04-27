package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CreateArticleRequest;
import app.SocialLibraryAPI.dto.response.ArticleDTO;
import app.SocialLibraryAPI.entity.ArticleEntity;
import app.SocialLibraryAPI.entity.ArticleRating;
import app.SocialLibraryAPI.entity.BookEntity;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.ArticleRepository;
import app.SocialLibraryAPI.repository.BookRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public ArticleDTO createArticle(String userEmail, CreateArticleRequest request) {
        log.info("Attempting to create article '{}' by user: {}", request.title(), userEmail);

        UserEntity author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        BookEntity relatedBook = null;
        if (request.relatedBookId() != null) {
            relatedBook = bookRepository.findById(request.relatedBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + request.relatedBookId()));
        }

        ArticleEntity article = new ArticleEntity();
        article.setTitle(request.title());
        article.setContent(request.content());
        article.setAuthor(author);
        article.setRelatedBook(relatedBook);
        article.setCreatedAt(LocalDateTime.now());

        ArticleEntity savedArticle = articleRepository.save(article);
        log.info("Successfully created article id: {}", savedArticle.getId());

        return mapToDTO(savedArticle);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDTO> getArticlesFeed(Pageable pageable) {
        log.info("Fetching article feed");
        return articleRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public ArticleDTO getArticleById(Integer id) {
        log.info("Fetching article with id: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Article not found with id: {}", id);
                    return new EntityNotFoundException("Article not found.");
                });
        return mapToDTO(article);
    }

    @Transactional
    public ArticleDTO updateArticle(String userEmail, Integer id, CreateArticleRequest request) {
        log.info("Attempting to update article id: {} by user: {}", id, userEmail);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found."));

        if (!article.getAuthor().getEmail().equals(userEmail)) {
            log.error("User {} attempted to update article {} owned by {}", userEmail, id, article.getAuthor().getEmail());
            throw new IllegalStateException("You can only edit your own articles!");
        }

        article.setTitle(request.title());
        article.setContent(request.content());

        if (request.relatedBookId() != null) {
            BookEntity relatedBook = bookRepository.findById(request.relatedBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found."));
            article.setRelatedBook(relatedBook);
        } else {
            article.setRelatedBook(null);
        }

        ArticleEntity updated = articleRepository.save(article);
        log.info("Successfully updated article id: {}", id);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteArticle(String userEmail, Integer id) {
        log.info("Attempting to delete article id: {} by user: {}", id, userEmail);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found."));

        if (!article.getAuthor().getEmail().equals(userEmail)) {
            log.error("User {} attempted to delete article {} owned by {}", userEmail, id, article.getAuthor().getEmail());
            throw new IllegalStateException("You can only delete your own articles!");
        }

        articleRepository.delete(article);
        log.info("Successfully deleted article id: {}", id);
    }

    private ArticleDTO mapToDTO(ArticleEntity entity) {
        float averageRating = 0.0f;
        if (entity.getRatings() != null && !entity.getRatings().isEmpty()) {
            double sum = entity.getRatings().stream().mapToDouble(ArticleRating::getScore).sum();
            averageRating = (float) (sum / entity.getRatings().size());
            averageRating = Math.round(averageRating * 10.0f) / 10.0f;
        }

        return new ArticleDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getAuthor().getFullName(),
                entity.getRelatedBook() != null ? entity.getRelatedBook().getId() : null,
                entity.getRelatedBook() != null ? entity.getRelatedBook().getTitle() : null,
                entity.getCreatedAt(),
                averageRating
        );
    }
}