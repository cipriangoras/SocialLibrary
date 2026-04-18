package app.SocialLibraryAPI.dto.response;
import java.time.LocalDateTime;

public record ReviewDTO(
        Integer id,
        String content,
        int rating,
        LocalDateTime createdAt,
        String authorName
) {}