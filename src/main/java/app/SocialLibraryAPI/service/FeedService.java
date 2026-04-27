package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.response.FeedItemDTO;
import app.SocialLibraryAPI.entity.ArticleEntity;
import app.SocialLibraryAPI.entity.ReviewEntity;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.ArticleRepository;
import app.SocialLibraryAPI.repository.ReviewRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FeedService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ArticleRepository articleRepository;

    public FeedService(UserRepository userRepository, ReviewRepository reviewRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional(readOnly = true)
    public List<FeedItemDTO> getUserFeed(String userEmail, LocalDateTime cursor, int limit) {
        log.info("Generating feed for user: {} with cursor: {}", userEmail, cursor);

        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> followingIds = currentUser.getFollowing().stream()
                .map(UserEntity::getId)
                .toList();

        if (followingIds.isEmpty()) {
            return List.of();
        }

        PageRequest limitRequest = PageRequest.of(0, limit);

        List<ReviewEntity> reviews = reviewRepository.findByUser_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);

        List<ArticleEntity> articles = articleRepository.findByAuthor_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);

        List<FeedItemDTO> feed = new ArrayList<>();

        for (ReviewEntity r : reviews) {
            feed.add(new FeedItemDTO(
                    "REVIEW",
                    r.getId(),
                    r.getUser().getFullName(),
                    r.getUser().getProfilePicUrl(),
                    r.getContent(),
                    r.getBook().getId(),
                    r.getBook().getTitle(),
                    r.getCreatedAt()
            ));
        }

        for (ArticleEntity a : articles) {
            feed.add(new FeedItemDTO(
                    "ARTICLE",
                    a.getId(),
                    a.getAuthor().getFullName(),
                    a.getAuthor().getProfilePicUrl(),
                    a.getTitle(),
                    a.getRelatedBook() != null ? a.getRelatedBook().getId() : null,
                    a.getRelatedBook() != null ? a.getRelatedBook().getTitle() : null,
                    a.getCreatedAt()
            ));
        }

        feed.sort(Comparator.comparing(FeedItemDTO::createdAt).reversed());

        return feed.stream().limit(limit).toList();
    }
}