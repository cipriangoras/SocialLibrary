package app.SocialLibraryAPI.dto.response;
import app.SocialLibraryAPI.entity.Status;

public record LibraryEntryDTO(
        Integer id,
        Integer bookId,
        String bookTitle,
        String coverImageUrl,
        Status status,
        boolean isFavorite
) {}