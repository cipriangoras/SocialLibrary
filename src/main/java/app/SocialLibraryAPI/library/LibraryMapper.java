package app.SocialLibraryAPI.library;

import app.SocialLibraryAPI.library.dto.LibraryEntryDTO;

public class LibraryMapper {
    public static LibraryEntryDTO toDTO(UserBookLibraryEntity entity) {
        return new LibraryEntryDTO(
                entity.getId(),
                entity.getBook().getId(),
                entity.getBook().getTitle(),
                entity.getBook().getCoverImageUrl(),
                entity.getStatus(),
                entity.isFavorite()
        );
    }
}