package app.SocialLibraryAPI.mappers;

import app.SocialLibraryAPI.dto.response.BookClubDTO;
import app.SocialLibraryAPI.dto.response.ClubSessionDTO;
import app.SocialLibraryAPI.entity.BookClubEntity;
import app.SocialLibraryAPI.entity.ClubSession;

public class BookClubMapper {
    public static BookClubDTO toClubDTO(BookClubEntity club) {
        return new BookClubDTO(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getUser().getId(),
                club.getUser().getFullName(),
                club.getBook().getId(),
                club.getBook().getTitle()
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
