package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record ArticleDTO(
        Integer id,
        String title,
        String content,
        String authorName,
        Long authorId,
        Integer relatedBookId,
        String relatedBookTitle,
        LocalDateTime createdAt,
        int likeCount,
        int commentCount
) {}