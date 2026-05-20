package app.SocialLibraryAPI.review.dto;
import java.time.LocalDateTime;

public record ReviewDTO(
        Integer id,
        String content,
        int rating,
        LocalDateTime createdAt,
        String authorName,
        int likeCount,
        int commentCount
) {}