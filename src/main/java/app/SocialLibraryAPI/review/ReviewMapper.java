package app.SocialLibraryAPI.review;

import app.SocialLibraryAPI.review.dto.ReviewDTO;

public class ReviewMapper {
    public static ReviewDTO toDTO(ReviewEntity review){

        int likes = (review.getLikes() != null) ? review.getLikes().size() : 0;
        int comments = (review.getComments() != null) ? review.getComments().size() : 0;

        return new ReviewDTO(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUser().getFullName(),
                likes,
                comments
        );
    }
}