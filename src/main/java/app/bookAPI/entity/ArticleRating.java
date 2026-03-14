package app.bookAPI.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "article_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"article_id", "user_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;
}
