package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.BookClubDTO;
import app.SocialLibraryAPI.dto.response.ClubSessionDTO;
import app.SocialLibraryAPI.entity.BookClubEntity;
import app.SocialLibraryAPI.entity.BookEntity;
import app.SocialLibraryAPI.entity.ClubSession;

import java.util.List;

public class BookClubMapper {
// În app.SocialLibraryAPI.mappers.BookClubMapper

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
                club.getAvgAttendance(),
                previousReads
        );
    }

    public static ClubSessionDTO toSessionDTO(ClubSession session) {
        return new ClubSessionDTO(
                session.getId(),
                session.getTitle(),
                session.getStartTime(),
                session.getBookClub().getId()
        );
    }
}
