package app.SocialLibraryAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "bookClub")
    private Set<Book_Club_Members> members;

    @OneToMany(mappedBy = "bookClub", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClubSession> sessions = new HashSet<>();


}
