package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.ArticleDTO;
import app.SocialLibraryAPI.entity.ArticleEntity;
import app.SocialLibraryAPI.entity.ArticleRating;

public class ArticleMapper {
    public static ArticleDTO toDTO(ArticleEntity entity) {
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
