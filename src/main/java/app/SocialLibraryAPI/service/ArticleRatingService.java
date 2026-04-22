package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.entity.ArticleEntity;
import app.SocialLibraryAPI.entity.ArticleRating;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.ArticleRatingRepository;
import app.SocialLibraryAPI.repository.ArticleRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ArticleRatingService {

    private final ArticleRatingRepository ratingRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleRatingService(ArticleRatingRepository ratingRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void rateArticle(String userEmail, Integer articleId, int score) {
        log.info("User {} is rating article id: {} with score: {}", userEmail, articleId, score);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + articleId));

        ArticleRating rating = ratingRepository.findByArticle_IdAndUser_Email(articleId, userEmail)
                .orElseGet(() -> {
                    ArticleRating newRating = new ArticleRating();
                    newRating.setArticle(article);
                    newRating.setUser(user);
                    return newRating;
                });

        rating.setScore(score);
        ratingRepository.save(rating);
        log.info("Successfully saved rating for article id: {}", articleId);
    }
}