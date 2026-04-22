package app.SocialLibraryAPI.dto.request;
import jakarta.validation.constraints.*;

public record CreateReviewRequest(
        @NotNull Integer bookId,
        @NotBlank @Size(max = 2000) String content,
        @Min(1) @Max(5) int rating
) {}