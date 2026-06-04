package app.SocialLibraryAPI.feed;

import app.SocialLibraryAPI.club.BookClubEntity;
import app.SocialLibraryAPI.club.BookClubRepository;
import app.SocialLibraryAPI.club.ClubSessionEntity;
import app.SocialLibraryAPI.club.ClubSessionRepository;
import app.SocialLibraryAPI.feed.dto.FeedItemDTO;
import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.library.UserBookLibraryRepository;
import app.SocialLibraryAPI.review.ReviewEntity;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.article.ArticleRepository;
import app.SocialLibraryAPI.review.ReviewRepository;
import app.SocialLibraryAPI.user.UserRepository;
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
    private final BookClubRepository bookClubRepository;
    private final ClubSessionRepository clubSessionRepository;
    private final UserBookLibraryRepository userBookLibraryRepository;

    public FeedService(UserRepository userRepository, ReviewRepository reviewRepository, ArticleRepository articleRepository, BookClubRepository bookClubRepository, ClubSessionRepository clubSessionRepository, UserBookLibraryRepository userBookLibraryRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.articleRepository = articleRepository;
        this.bookClubRepository = bookClubRepository;
        this.clubSessionRepository = clubSessionRepository;
        this.userBookLibraryRepository = userBookLibraryRepository;
    }

    @Transactional(readOnly = true)
    public List<FeedItemDTO> getUserFeed(String userEmail, LocalDateTime cursor, int limit) {
        log.info("Generating feed for user: {} with cursor: {}", userEmail, cursor);

        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (cursor == null) {
            cursor = LocalDateTime.now().plusYears(10);
        }

        List<Long> followingIds = currentUser.getFollowing().stream()
                .map(UserEntity::getId)
                .toList();

        if (followingIds.isEmpty()) {
            return List.of();
        }

        List<FeedItemDTO> feed = new ArrayList<>();
        PageRequest limitRequest = PageRequest.of(0, limit);

        List<ReviewEntity> reviews = reviewRepository.findByUser_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);
        for (var r : reviews) feed.add(FeedMapper.toDTO(r));

        List<ArticleEntity> articles = articleRepository.findByAuthor_IdInAndCreatedAtLessThanOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);
        for (var a : articles) feed.add(FeedMapper.toDTO(a));

        List<BookClubEntity> clubs = bookClubRepository.findByUser_IdInAndCreatedAtBeforeOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);
        for (var c : clubs) feed.add(FeedMapper.toDTO(c));

        List<ClubSessionEntity> sessions = clubSessionRepository.findByBookClub_User_IdInAndCreatedAtBeforeOrderByCreatedAtDesc(
                followingIds, cursor, limitRequest);
        for (var s : sessions) {
            feed.add(FeedMapper.toDTO(s));
        }

        List<UserBookLibraryEntity> libraryUpdates = userBookLibraryRepository.findByUser_IdInAndUpdatedAtLessThanOrderByUpdatedAtDesc(
                followingIds, cursor, limitRequest);
        for (var l : libraryUpdates) {
            if (l.getUpdatedAt() != null) {
                feed.add(FeedMapper.toDTO(l));
            }
        }

        feed.sort(Comparator.comparing(FeedItemDTO::createdAt).reversed());

        return feed.stream().limit(limit).toList();
    }
}