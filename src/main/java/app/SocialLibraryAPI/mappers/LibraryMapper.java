package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.LibraryEntryDTO;
import app.SocialLibraryAPI.entity.UserBookLibraryEntity;

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