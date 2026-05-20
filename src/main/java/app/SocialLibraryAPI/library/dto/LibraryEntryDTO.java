package app.SocialLibraryAPI.library.dto;
import app.SocialLibraryAPI.library.Status;

public record LibraryEntryDTO(
        Integer id,
        Integer bookId,
        String bookTitle,
        String coverImageUrl,
        Status status,
        boolean isFavorite
) {}