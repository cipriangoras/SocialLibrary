package app.SocialLibraryAPI.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private int age;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;
    private String profilePicUrl;
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private Set<UserBookLibraryEntity> userBookLibraries;

    @OneToMany(mappedBy = "user")
    private Set<Book_Club_Members> bookClubMemberships;

    @OneToMany(mappedBy = "user")
    private Set<ChatMessage> chatMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<Article> articles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ArticleRating> articleRatings = new HashSet<>();

    public UserEntity() {
    }

    public UserEntity(Long id, String username, int age, String email, String profilePicUrl, String password, String bio, Role role) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.password = password;
        this.bio = bio;
        this.role = role;
    }

}
