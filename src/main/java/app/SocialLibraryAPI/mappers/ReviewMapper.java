package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.ReviewDTO;
import app.SocialLibraryAPI.entity.ReviewEntity;

public class ReviewMapper {
    public static ReviewDTO toDTO(ReviewEntity review){
        return new ReviewDTO(
            review.getId(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt(),
            review.getUser().getFullName()
        );
    }
}
