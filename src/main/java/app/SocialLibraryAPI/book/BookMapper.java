package app.SocialLibraryAPI.book;

import app.SocialLibraryAPI.book.dto.BookDTO;
import app.SocialLibraryAPI.genre.dto.GenreDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
public class BookMapper {
    public static BookDTO toDTO(BookEntity book) {
        if (book == null) return null;

        var genresDTO = book.getGenres().stream()
                .map(g -> new GenreDTO(g.getId(), g.getName()))
                .collect(Collectors.toSet());

        int reviewsCount = (book.getReviews() != null) ? book.getReviews().size() : 0;

        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getPublicationYear(),
                book.getCoverImageUrl(),
                book.getRating(),
                genresDTO,
                reviewsCount
        );
    }
}