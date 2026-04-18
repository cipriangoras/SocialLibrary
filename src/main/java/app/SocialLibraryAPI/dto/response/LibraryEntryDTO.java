package app.SocialLibraryAPI.dto.response;
import app.SocialLibraryAPI.entity.Status;

public record LibraryEntryDTO(
        Integer id,
        Integer bookId,
        String bookTitle,
        Status status,
        boolean isFavorite
) {}