package app.SocialLibraryAPI.article;

import app.SocialLibraryAPI.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "article_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"article_id", "user_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}