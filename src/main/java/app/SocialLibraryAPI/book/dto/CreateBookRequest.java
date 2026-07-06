package app.SocialLibraryAPI.book.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record CreateBookRequest (
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank @Size(max = 150)
        String author,

        @NotBlank
        @Size(max = 255, message = "ISBN-ul nu poate depăși 255 de caractere")
        String isbn,

        @Size(max = 10000)
        String description,

        @PastOrPresent
        LocalDate publicationYear,

        @Size(max = 1000)
        //@URL
        String coverImageUrl,

        Set<Integer> genreIds
){
}
