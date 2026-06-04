package app.SocialLibraryAPI.feed;

import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.club.BookClubEntity;
import app.SocialLibraryAPI.club.ClubSessionEntity;
import app.SocialLibraryAPI.feed.dto.FeedItemDTO;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.review.ReviewEntity;

import java.time.LocalDateTime;

public class FeedMapper {
    public static FeedItemDTO toDTO(ReviewEntity review){
        return new FeedItemDTO(
                review.getId(),
                FeedType.REVIEW,
                review.getUser().getFullName(),
                review.getUser().getProfilePicUrl(),
                "Review",
                review.getContent(),
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getBook().getCoverImageUrl(),
                review.getCreatedAt()
        );
    }
    public static FeedItemDTO toDTO(ArticleEntity article){
        return new FeedItemDTO(
                article.getId(),
                FeedType.ARTICLE,
                article.getAuthor().getFullName(),
                article.getAuthor().getProfilePicUrl(),
                article.getTitle(),
                article.getContent(),
                article.getRelatedBook() != null ? article.getRelatedBook().getId() : null,
                article.getRelatedBook() != null ? article.getRelatedBook().getTitle() : null,
                article.getRelatedBook() != null ? article.getRelatedBook().getCoverImageUrl() : null,
                article.getCreatedAt()
        );
    }
    public static FeedItemDTO toDTO(BookClubEntity club){
        return new FeedItemDTO(
                club.getId(),
                FeedType.CLUB_CREATED,
                club.getUser().getFullName(),
                club.getUser().getProfilePicUrl(),
                club.getName(),
                club.getDescription(),
                club.getBook().getId(),
                club.getBook().getTitle(),
                club.getBook().getCoverImageUrl(),
                club.getCreatedAt()
        );
    }

    public static FeedItemDTO toDTO(ClubSessionEntity session){
        return new FeedItemDTO(
                session.getId(),
                FeedType.CLUB_SESSION,
                "Club Moderator",
                session.getBookClub().getBook().getCoverImageUrl(),
                "Sesiune nouă programată în " + session.getBookClub().getName(),
                "Tema discuției: " + session.getTitle() + " | Începe la: " + session.getStartTime(),
                null,
                null,
                null,
                session.getCreatedAt() != null ? session.getCreatedAt() : session.getStartTime() // <-- ACTUALIZAT AICI
        );
    }

    public static FeedItemDTO toDTO(UserBookLibraryEntity library) {
        String statusText = switch (library.getStatus()) {
            case WANT_TO_READ -> "Vrea să citească";
            case READING -> "Citește acum";
            case COMPLETED -> "A terminat de citit";
        };

        return new FeedItemDTO(
                library.getId(),
                FeedType.LIBRARY_UPDATE,
                library.getUser().getFullName(),
                library.getUser().getProfilePicUrl(),
                "Status Actualizat",
                statusText,
                library.getBook().getId(),
                library.getBook().getTitle(),
                library.getBook().getCoverImageUrl(),
                library.getUpdatedAt() != null ? library.getUpdatedAt() : LocalDateTime.now()
        );
    }


}
