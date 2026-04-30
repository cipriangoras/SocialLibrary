package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CommentRequest;
import app.SocialLibraryAPI.dto.response.CommentResponseDTO;
import app.SocialLibraryAPI.entity.*;
import app.SocialLibraryAPI.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InteractionService {

    private final ArticleLikeRepository articleLikeRepo;
    private final ReviewLikeRepository reviewLikeRepo;
    private final ArticleCommentRepository articleCommentRepo;
    private final ReviewCommentRepository reviewCommentRepo;
    private final ArticleRepository articleRepo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    public InteractionService(ArticleLikeRepository articleLikeRepo, ReviewLikeRepository reviewLikeRepo, ArticleCommentRepository articleCommentRepo, ReviewCommentRepository reviewCommentRepo, ArticleRepository articleRepo, ReviewRepository reviewRepo, UserRepository userRepo) {
        this.articleLikeRepo = articleLikeRepo;
        this.reviewLikeRepo = reviewLikeRepo;
        this.articleCommentRepo = articleCommentRepo;
        this.reviewCommentRepo = reviewCommentRepo;
        this.articleRepo = articleRepo;
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void toggleArticleLike(String userEmail, Integer articleId) {
        log.info("User {} toggling like for article id: {}", userEmail, articleId);

        Optional<ArticleLikeEntity> existingLike = articleLikeRepo.findByArticle_IdAndUser_Email(articleId, userEmail);

        if (existingLike.isPresent()) {
            articleLikeRepo.delete(existingLike.get()); // Dislike
            log.info("Removed like from article {}", articleId);
        } else {
            UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
            ArticleEntity article = articleRepo.findById(articleId)
                    .orElseThrow(() -> new EntityNotFoundException("Article not found"));

            ArticleLikeEntity newLike = new ArticleLikeEntity();
            newLike.setUser(user);
            newLike.setArticle(article);
            articleLikeRepo.save(newLike); // Like
            log.info("Added like to article {}", articleId);
        }
    }

    @Transactional
    public void toggleReviewLike(String userEmail, Integer reviewId) {
        log.info("User {} toggling like for review id: {}", userEmail, reviewId);

        Optional<ReviewLikeEntity> existingLike = reviewLikeRepo.findByReview_IdAndUser_Email(reviewId, userEmail);

        if (existingLike.isPresent()) {
            reviewLikeRepo.delete(existingLike.get());
        } else {
            UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
            ReviewEntity review = reviewRepo.findById(reviewId)
                    .orElseThrow(() -> new EntityNotFoundException("Review not found"));

            ReviewLikeEntity newLike = new ReviewLikeEntity();
            newLike.setUser(user);
            newLike.setReview(review);
            reviewLikeRepo.save(newLike);
        }
    }


    @Transactional
    public CommentResponseDTO addArticleComment(String userEmail, Integer articleId, CommentRequest request) {
        UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
        ArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        ArticleCommentEntity comment = new ArticleCommentEntity();
        comment.setUser(user);
        comment.setArticle(article);
        comment.setContent(request.content());
        comment.setCreatedAt(LocalDateTime.now());

        ArticleCommentEntity saved = articleCommentRepo.save(comment);
        return new CommentResponseDTO(saved.getId(), user.getFullName(), user.getProfilePicUrl(), saved.getContent(), saved.getCreatedAt());
    }

    @Transactional
    public CommentResponseDTO addReviewComment(String userEmail, Integer reviewId, CommentRequest request) {
        UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
        ReviewEntity review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        ReviewCommentEntity comment = new ReviewCommentEntity();
        comment.setUser(user);
        comment.setReview(review);
        comment.setContent(request.content());
        comment.setCreatedAt(LocalDateTime.now());

        ReviewCommentEntity saved = reviewCommentRepo.save(comment);
        return new CommentResponseDTO(saved.getId(), user.getFullName(), user.getProfilePicUrl(), saved.getContent(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getArticleComments(Integer articleId) {
        return articleCommentRepo.findByArticle_IdOrderByCreatedAtAsc(articleId).stream()
                .map(c -> new CommentResponseDTO(c.getId(), c.getUser().getFullName(), c.getUser().getProfilePicUrl(), c.getContent(), c.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getReviewComments(Integer reviewId) {
        return reviewCommentRepo.findByReview_IdOrderByCreatedAtAsc(reviewId).stream()
                .map(c -> new CommentResponseDTO(c.getId(), c.getUser().getFullName(), c.getUser().getProfilePicUrl(), c.getContent(), c.getCreatedAt()))
                .collect(Collectors.toList());
    }
}