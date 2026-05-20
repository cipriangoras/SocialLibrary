package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.club.dto.ClubSessionDTO;
import app.SocialLibraryAPI.book.BookEntity;

import java.util.List;

public class BookClubMapper {
    public static BookClubDTO toClubDTO(BookClubEntity club) {

        List<String> previousReads = club.getPastBooks().stream()
                .map(BookEntity::getTitle)
                .toList();

        int discussions = (club.getSessions() != null) ? club.getSessions().size() : 0;

        int booksCompleted = (club.getPastBooks() != null) ? club.getPastBooks().size() : 0;

        return new BookClubDTO(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getClubGuidelines(),
                club.getCreatedAt(),
                club.getUser().getId(),
                club.getUser().getFullName(),
                club.getBook().getId(),
                club.getBook().getTitle(),
                booksCompleted,
                discussions,
                club.getMemberCount(),
                club.getAvgAttendance(),
                previousReads
        );
    }

    public static ClubSessionDTO toSessionDTO(ClubSessionEntity session) {
        return new ClubSessionDTO(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartTime(),
                session.getBookClub().getId()
        );
    }
}
