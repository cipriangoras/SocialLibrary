package app.SocialLibraryAPI.book;

import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.club.BookClubEntity;
import app.SocialLibraryAPI.genre.GenreEntity;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.review.ReviewEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate publicationYear;

    @Column(length = 1000)
    private String coverImageUrl;

    private float rating;

    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<GenreEntity> genres = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "book")
    private Set<UserBookLibraryEntity> userBookLibraries;

    @OneToMany(mappedBy = "book")
    private Set<BookClubEntity> bookClubs;

    @OneToMany(mappedBy = "relatedBook")
    private Set<ArticleEntity> articles = new HashSet<>();

    public void addGenre(GenreEntity genre) {
        this.genres.add(genre);
        genre.getBooks().add(this);
    }

    public void removeGenre(GenreEntity genre) {
        this.genres.remove(genre);
        genre.getBooks().remove(this);
    }
}