package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.ArticleDTO;
import app.SocialLibraryAPI.entity.ArticleEntity;

public class ArticleMapper {
    public static ArticleDTO toDTO(ArticleEntity entity) {
        int likes = (entity.getLikes() != null) ? entity.getLikes().size() : 0;
        int comments = (entity.getComments() != null) ? entity.getComments().size() : 0;

        return new ArticleDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getAuthor().getFullName(),
                entity.getAuthor().getId(),
                entity.getRelatedBook() != null ? entity.getRelatedBook().getId() : null,
                entity.getRelatedBook() != null ? entity.getRelatedBook().getTitle() : null,
                entity.getCreatedAt(),
                likes,
                comments
        );
    }
}