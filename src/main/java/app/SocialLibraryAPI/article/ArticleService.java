package app.SocialLibraryAPI.article;

import app.SocialLibraryAPI.article.dto.CreateArticleRequest;
import app.SocialLibraryAPI.article.dto.ArticleDTO;
import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.book.BookRepository;
import app.SocialLibraryAPI.user.UserRepository;
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

        return ArticleMapper.toDTO(savedArticle);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDTO> getArticlesFeed(Pageable pageable) {
        log.info("Fetching article feed");
        return articleRepository.findAllByOrderByCreatedAtDesc(pageable).map(ArticleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ArticleDTO getArticleById(Integer id) {
        log.info("Fetching article with id: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Article not found with id: {}", id);
                    return new EntityNotFoundException("Article not found.");
                });
        return ArticleMapper.toDTO(article);
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
        return ArticleMapper.toDTO(updated);
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

    @Transactional(readOnly = true)
    public Page<ArticleDTO> getArticlesByAuthorId(Long authorId, Pageable pageable) {
        log.info("Fetching articles for author ID: {}", authorId);

        if (!userRepository.existsById(authorId)) {
            log.error("Author not found with id: {}", authorId);
            throw new EntityNotFoundException("Author not found with id: " + authorId);
        }

        return articleRepository.findByAuthor_Id(authorId, pageable)
                .map(ArticleMapper::toDTO);
    }


    public Page<ArticleDTO> getArticlesByBookTitle(String bookTitle, Pageable pageable) {
        Page<ArticleEntity> articlesPage;
        log.info("Fetching articles bt book title : {}", bookTitle);

        if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            articlesPage = articleRepository.findByRelatedBook_TitleContainingIgnoreCase(bookTitle, pageable);
        } else {
            articlesPage = articleRepository.findAll(pageable);
        }

        return articlesPage.map(ArticleMapper::toDTO);
    }

}