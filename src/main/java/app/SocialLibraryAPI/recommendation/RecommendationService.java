package app.SocialLibraryAPI.recommendation;

import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.article.ArticleMapper;
import app.SocialLibraryAPI.article.ArticleRepository;
import app.SocialLibraryAPI.article.dto.ArticleDTO;
import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.book.BookRepository;
import app.SocialLibraryAPI.book.dto.BookDTO;
import app.SocialLibraryAPI.book.BookMapper;
import app.SocialLibraryAPI.club.BookClubEntity;
import app.SocialLibraryAPI.club.BookClubMapper;
import app.SocialLibraryAPI.club.BookClubRepository;
import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.genre.GenreEntity;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.user.UserMapper;
import app.SocialLibraryAPI.user.UserRepository;
import app.SocialLibraryAPI.user.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ArticleRepository articleRepository;
    private final BookClubRepository bookClubRepository;

    public RecommendationService(UserRepository userRepository,
                                 BookRepository bookRepository,
                                 ArticleRepository articleRepository,
                                 BookClubRepository bookClubRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.articleRepository = articleRepository;
        this.bookClubRepository = bookClubRepository;
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getBookRecommendations(String userEmail, int limit) {
        log.info("Generating up to {} book recommendations for user: {}", limit, userEmail);

        if (limit <= 0){
            return Collections.emptyList();
        }

        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to fetch book recommendations. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        List<Integer> excludedBookIds = currentUser.getUserBookLibraries().stream()
                .map(lib -> lib.getBook().getId())
                .collect(Collectors.toList());

        if (excludedBookIds.isEmpty()) {
            excludedBookIds.add(-1);
        }

        Set<BookEntity> recommendedBooks = new LinkedHashSet<>();

        List<BookEntity> socialBooks = new ArrayList<>();
        List<Long> followingIds = currentUser.getFollowing().stream()
                .map(UserEntity::getId)
                .toList();

        if (!followingIds.isEmpty()) {
            socialBooks = bookRepository.findRecommendedBooksFromFollowing(
                    followingIds, excludedBookIds, PageRequest.of(0, limit)
            );
        }

        List<BookEntity> genreBooks = new ArrayList<>();
        List<Integer> favoriteGenreIds = currentUser.getUserBookLibraries().stream()
                .filter(UserBookLibraryEntity::isFavorite)
                .flatMap(lib -> lib.getBook().getGenres().stream())
                .map(GenreEntity::getId)
                .distinct()
                .toList();

        if (!favoriteGenreIds.isEmpty()) {
            genreBooks = bookRepository.findRecommendedBooksByGenres(
                    favoriteGenreIds, excludedBookIds, PageRequest.of(0, limit)
            );
        }

        int socialIndex = 0;
        int genreIndex = 0;

        for (int i = 0; recommendedBooks.size() < limit && (socialIndex < socialBooks.size() || genreIndex < genreBooks.size()); i++) {
            if (i % 2 == 0) {
                if (socialIndex < socialBooks.size()) {
                    recommendedBooks.add(socialBooks.get(socialIndex++));
                } else {
                    recommendedBooks.add(genreBooks.get(genreIndex++));
                }
            } else {
                if (genreIndex < genreBooks.size()) {
                    recommendedBooks.add(genreBooks.get(genreIndex++));
                } else {
                    recommendedBooks.add(socialBooks.get(socialIndex++));
                }
            }
        }

        if (recommendedBooks.size() < limit) {
            int remainingLimit = limit - recommendedBooks.size();
            List<BookEntity> trendingBooks = bookRepository.findTrendingBooks(
                    excludedBookIds, PageRequest.of(0, remainingLimit)
            );
            recommendedBooks.addAll(trendingBooks);
        }

        log.info("Successfully found {} book recommendations for user: {}", recommendedBooks.size(), userEmail);

        return recommendedBooks.stream()
                .limit(limit)
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUserRecommendations(String userEmail, int limit) {
        log.info("Generating up to {} user recommendations for user: {}", limit, userEmail);

        if (limit <= 0) return Collections.emptyList();

        UserEntity me = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to fetch user recommendations. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        List<Long> myFollowingIds = me.getFollowing().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        if (myFollowingIds.isEmpty()) myFollowingIds.add(-1L);

        List<UserEntity> mutuals = userRepository.findMutualConnections(
                myFollowingIds, me.getId(), PageRequest.of(0, limit)
        );

        log.info("Successfully found {} user recommendations for user: {}", mutuals.size(), userEmail);

        return mutuals.stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getArticleRecommendations(String userEmail, int limit) {
        log.info("Generating up to {} article recommendations for user: {}", limit, userEmail);

        if (limit <= 0) return Collections.emptyList();

        UserEntity me = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to fetch article recommendations. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        List<Integer> myLibraryBookIds = me.getUserBookLibraries().stream()
                .map(lib -> lib.getBook().getId())
                .collect(Collectors.toList());

        if (myLibraryBookIds.isEmpty()) myLibraryBookIds.add(-1);

        List<ArticleEntity> contextualArticles = articleRepository.findArticlesRelatedToUserBooks(
                myLibraryBookIds, PageRequest.of(0, limit)
        );
        Set<ArticleEntity> recommendedArticles = new LinkedHashSet<>(contextualArticles);

        if (recommendedArticles.size() < limit) {
            int remaining = limit - recommendedArticles.size();
            recommendedArticles.addAll(
                    articleRepository.findTrendingArticles(PageRequest.of(0, remaining))
            );
        }

        log.info("Successfully found {} article recommendations for user: {}", recommendedArticles.size(), userEmail);

        return recommendedArticles.stream().map(ArticleMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookClubDTO> getClubRecommendations(String userEmail, int limit) {
        log.info("Generating up to {} club recommendations for user: {}", limit, userEmail);

        if (limit <= 0) return Collections.emptyList();

        UserEntity me = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to fetch club recommendations. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        List<Long> myFollowingIds = me.getFollowing().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        if (myFollowingIds.isEmpty()) myFollowingIds.add(-1L);

        List<Integer> myClubIds = me.getBookClubMemberships().stream()
                .map(membership -> membership.getBookClub().getId())
                .collect(Collectors.toList());

        if (myClubIds.isEmpty()) myClubIds.add(-1);

        List<BookClubEntity> socialClubs = bookClubRepository.findClubsWithFriends(
                myFollowingIds, myClubIds, PageRequest.of(0, limit)
        );
        Set<BookClubEntity> recommendedClubs = new LinkedHashSet<>(socialClubs);

        log.info("Successfully found {} club recommendations for user: {}", recommendedClubs.size(), userEmail);

        return recommendedClubs.stream().map(BookClubMapper::toClubDTO).collect(Collectors.toList());
    }
}