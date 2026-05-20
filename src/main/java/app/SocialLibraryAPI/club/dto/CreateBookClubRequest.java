package app.SocialLibraryAPI.club.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookClubRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 1000) String description,
        @Size(max = 2000) String clubGuidelines,
        @NotNull Integer bookId
) {}