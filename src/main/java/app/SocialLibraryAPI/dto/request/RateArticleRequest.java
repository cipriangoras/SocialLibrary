package app.SocialLibraryAPI.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RateArticleRequest(
        @Min(1) @Max(5) int score
) {}