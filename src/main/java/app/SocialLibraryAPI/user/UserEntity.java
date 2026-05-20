package app.SocialLibraryAPI.user;


import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.club.BookClubMembersEntity;
import app.SocialLibraryAPI.club.ChatMessageEntity;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.review.ReviewEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private int age;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;
    private String profilePicUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Set<UserBookLibraryEntity> userBookLibraries = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<BookClubMembersEntity> bookClubMemberships = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ChatMessageEntity> chatMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<ArticleEntity> articles = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> followers = new HashSet<>();

    public void follow(UserEntity userToFollow) {
        this.following.add(userToFollow);
        userToFollow.getFollowers().add(this);
    }

    public void unfollow(UserEntity userToUnfollow) {
        this.following.remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(this);
    }

    // UserDetails methods

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword(){
        return password;
    }



}
