package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record ArticleDTO(
        Integer id,
        String title,
        String content,
        String authorName,
        Integer relatedBookId,
        String relatedBookTitle,
        LocalDateTime createdAt,
        float averageRating
) {}