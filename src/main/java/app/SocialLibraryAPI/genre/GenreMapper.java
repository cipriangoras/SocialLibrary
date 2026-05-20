package app.SocialLibraryAPI.genre;

import app.SocialLibraryAPI.genre.dto.GenreDTO;

public class GenreMapper {
    public static GenreDTO toDTO(GenreEntity genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }
}
