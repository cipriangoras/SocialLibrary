package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.BookDTO;
import app.SocialLibraryAPI.dto.response.GenreDTO;
import app.SocialLibraryAPI.entity.BookEntity;

import java.util.stream.Collectors;

public class BookMapper {
    public static BookDTO toDTO(BookEntity book) {
        if (book == null) return null;

        var genresDTO = book.getGenres().stream()
                .map(g -> new GenreDTO(g.getId(), g.getName()))
                .collect(Collectors.toSet());

        return new BookDTO(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getPublicationYear(),
                book.getCoverImageUrl(),
                book.getRating(),
                genresDTO
        );

    }
}