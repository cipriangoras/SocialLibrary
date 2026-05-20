package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_club_members")
public class BookClubMembersEntity {

    @EmbeddedId
    private BookClubMembersIdEntity id;

    @ManyToOne
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    private BookClubEntity bookClub;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime joinedAt;
}
