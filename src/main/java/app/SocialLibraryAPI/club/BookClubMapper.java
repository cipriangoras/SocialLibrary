package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.club.dto.ClubSessionDTO;
import app.SocialLibraryAPI.book.BookEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class BookClubMapper {
    public static BookClubDTO toClubDTO(BookClubEntity club) {

        List<String> previousReads = club.getPastBooks().stream()
                .map(BookEntity::getTitle)
                .toList();

        int discussions = (club.getSessions() != null) ? club.getSessions().size() : 0;
        int booksCompleted = (club.getPastBooks() != null) ? club.getPastBooks().size() : 0;

        String frequency = "Nou / Insuficiente date";
        if (club.getSessions() != null && club.getSessions().size() > 1) {
            List<ClubSessionEntity> sortedSessions = club.getSessions().stream()
                    .sorted(Comparator.comparing(ClubSessionEntity::getStartTime))
                    .toList();

            LocalDateTime firstSession = sortedSessions.get(0).getStartTime();
            LocalDateTime lastSession = sortedSessions.get(sortedSessions.size() - 1).getStartTime();

            long daysBetween = ChronoUnit.DAYS.between(firstSession, lastSession);
            if (daysBetween == 0) daysBetween = 1; // Prevenim împărțirea la zero

            double avgDays = (double) daysBetween / (sortedSessions.size() - 1);

            if (avgDays <= 4) {
                frequency = "Foarte Activ (Zilnic)";
            } else if (avgDays <= 14) {
                frequency = "Săptămânal";
            } else if (avgDays <= 40) {
                frequency = "Lunar";
            } else {
                frequency = "Ocazional";
            }
        } else if (club.getSessions() != null && club.getSessions().size() == 1) {
            frequency = "La început (1 sesiune)";
        }

        return new BookClubDTO(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getClubGuidelines(),
                club.getCreatedAt(),
                club.getUser().getId(),
                club.getUser().getFullName(),
                club.getBook() != null ? club.getBook().getId() : null,
                club.getBook() != null ? club.getBook().getTitle() : null,
                club.getBook() != null ? club.getBook().getCoverImageUrl() : null,
                booksCompleted,
                discussions,
                club.getMemberCount(),
                club.getAvgAttendance(),
                previousReads,
                frequency
        );
    }

    public static ClubSessionDTO toSessionDTO(ClubSessionEntity session) {
        return new ClubSessionDTO(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartTime(),
                session.getBookClub().getId(),
                session.isActive()
        );
    }
}