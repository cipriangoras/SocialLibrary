package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String clubGuidelines;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private float avgAttendance = 0.0f;

    private int memberCount = 0;

    @ManyToMany
    @JoinTable(
            name = "book_club_past_books",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<BookEntity> pastBooks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @OneToMany(mappedBy = "bookClub", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookClubMembersEntity> members = new HashSet<>();

    @OneToMany(mappedBy = "bookClub", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClubSessionEntity> sessions = new HashSet<>();




}
