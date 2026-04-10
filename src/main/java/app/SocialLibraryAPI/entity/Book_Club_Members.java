package app.SocialLibraryAPI.entity;

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
public class Book_Club_Members {

    @EmbeddedId
    private BookClubMembersId id;

    @ManyToOne
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    private BookClub bookClub;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime joinedAt;
}
