package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CreateReviewRequest;
import app.SocialLibraryAPI.dto.response.ReviewDTO;
import app.SocialLibraryAPI.entity.BookEntity;
import app.SocialLibraryAPI.entity.ReviewEntity;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.repository.BookRepository;
import app.SocialLibraryAPI.repository.ReviewRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewDTO addReview(String userEmail, CreateReviewRequest request) {
        log.info("Attempting to add review for book id: {} by user: {}", request.bookId(), userEmail);

        if (reviewRepository.existsByBook_IdAndUser_Email(request.bookId(), userEmail)) {
            log.error("Failed to add review. User {} already reviewed book id: {}", userEmail, request.bookId());
            throw new IllegalStateException("You have already reviewed this book!");
        }

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to add review. User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        BookEntity book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> {
                    log.error("Failed to add review. Book not found with id: {}", request.bookId());
                    return new EntityNotFoundException("Book not found with id: " + request.bookId());
                });

        ReviewEntity review = new ReviewEntity();
        review.setContent(request.content());
        review.setRating(request.rating());
        review.setCreatedAt(LocalDateTime.now());
        review.setBook(book);
        review.setUser(user);

        ReviewEntity savedReview = reviewRepository.save(review);
        log.info("Successfully added review id: {} for book id: {}", savedReview.getId(), request.bookId());

        updateBookAverageRating(book);

        return new ReviewDTO(savedReview.getId(), savedReview.getContent(), savedReview.getRating(), savedReview.getCreatedAt(), user.getFullName());
    }

    @Transactional
    public void deleteReview(String userEmail, Integer reviewId) {
        log.info("Attempting to delete review id: {} by user: {}", reviewId, userEmail);
        ReviewEntity review = reviewRepository.findByIdAndUser_Email(reviewId, userEmail)
                .orElseThrow(() -> {
                    log.error("Failed to delete review. Review id {} not found or does not belong to user: {}", reviewId, userEmail);
                    return new EntityNotFoundException("Review not found or does not belong to you!");
                });

        BookEntity book = review.getBook();

        reviewRepository.delete(review);
        log.info("Successfully deleted review id: {}", reviewId);

        updateBookAverageRating(book);
    }

    private void updateBookAverageRating(BookEntity book) {
        log.info("Recalculating average rating for book id: {}", book.getId());

        List<ReviewEntity> reviews = reviewRepository.findByBook_Id(book.getId());

        if (reviews.isEmpty()) {
            book.setRating(0.0f);
            log.debug("No reviews left for book id: {}. Setting rating to 0.0", book.getId());
        } else {
            double sum = reviews.stream()
                    .mapToDouble(ReviewEntity::getRating)
                    .sum();

            float average = (float) (sum / reviews.size());
            average = Math.round(average * 10.0f) / 10.0f;

            book.setRating(average);
            log.debug("New average rating for book id: {} is {}", book.getId(), average);
        }

        bookRepository.save(book);
    }

    public Page<ReviewDTO> getReviewsForBook(Integer bookId, Pageable pageable) {
        log.info("Fetching reviews for book id: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }

        return reviewRepository.findByBook_Id(bookId, pageable)
                .map(review -> new ReviewDTO(
                        review.getId(),
                        review.getContent(),
                        review.getRating(),
                        review.getCreatedAt(),
                        review.getUser().getFullName()
                ));
    }

    @Transactional
    public ReviewDTO updateReview(String userEmail, Integer reviewId, CreateReviewRequest request) {
        log.info("Attempting to update review id: {} by user: {}", reviewId, userEmail);

        ReviewEntity review = reviewRepository.findByIdAndUser_Email(reviewId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Review not found or does not belong to you!"));

        review.setContent(request.content());
        review.setRating(request.rating());

        ReviewEntity updatedReview = reviewRepository.save(review);

        updateBookAverageRating(updatedReview.getBook());

        log.info("Successfully updated review id: {}", reviewId);
        return new ReviewDTO(
                updatedReview.getId(),
                updatedReview.getContent(),
                updatedReview.getRating(),
                updatedReview.getCreatedAt(),
                updatedReview.getUser().getFullName()
        );
    }
}