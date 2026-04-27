package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.GenreDTO;
import app.SocialLibraryAPI.entity.GenreEntity;

public class GenreMapper {
    public static GenreDTO toDTO(GenreEntity genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }
}
