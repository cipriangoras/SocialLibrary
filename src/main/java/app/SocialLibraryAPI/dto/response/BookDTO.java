package app.SocialLibraryAPI.dto.response;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

public record BookDTO(
        Integer id,
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank @Size(max = 150)
        String author,

        @NotBlank
        @Size(min = 10, max = 17)
        //@Pattern(regexp = "^[0-9-]+$", message = "ISBN must contain only digits and dashes")
        String isbn,

        @Size(max = 2000)
        String description,

        @PastOrPresent
        LocalDate publicationYear,

        @Size(max = 500) @URL
        String coverImageUrl,

        @DecimalMin("0.0") @DecimalMax("5.0")
        float averageRating,

        Set<GenreDTO> genres,
        int totalRatingsCount
) {}
