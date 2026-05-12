package app.SocialLibraryAPI.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(min = 3, max = 100) String fullName,
        @Min(0) @Max(120) int age,
        @Size(max = 500) String bio,
        @Size(max = 2048) String profilePicUrl
) {}