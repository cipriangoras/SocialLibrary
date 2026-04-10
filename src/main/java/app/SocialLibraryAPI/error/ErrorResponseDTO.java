package app.SocialLibraryAPI.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        String detailedMessage,
        LocalDateTime time
) {
}
