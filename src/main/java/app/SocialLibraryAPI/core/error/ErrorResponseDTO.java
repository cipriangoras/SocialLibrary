package app.SocialLibraryAPI.core.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        String detailedMessage,
        LocalDateTime time
) {
}
