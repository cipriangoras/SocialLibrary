package app.SocialLibraryAPI.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private String description;

    private LocalDateTime publicationYear;

    private String coverImageUrl;

    private float rating;


    @ManyToMany(mappedBy = "books")
    private Set<Genre> genres;


    @OneToMany(mappedBy = "book")
    private List<Review> reviews;

    @OneToMany(mappedBy = "book")
    private Set<UserBookLibraryEntity> userBookLibraries;

    @OneToMany(mappedBy = "book")
    private Set<BookClub> bookClubs;

    @OneToMany(mappedBy = "relatedBook")
    private Set<Article> articles = new HashSet<>();

}
