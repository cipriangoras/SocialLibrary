package app.SocialLibraryAPI.dto.response;
import java.time.LocalDateTime;

public record CommentResponseDTO(
        Integer id,
        String authorName,
        String authorProfilePicUrl,
        String content,
        LocalDateTime createdAt
) {}