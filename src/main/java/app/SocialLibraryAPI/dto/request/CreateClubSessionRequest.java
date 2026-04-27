package app.SocialLibraryAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateClubSessionRequest(
        @NotBlank String title,
        @NotNull LocalDateTime startTime
) {}