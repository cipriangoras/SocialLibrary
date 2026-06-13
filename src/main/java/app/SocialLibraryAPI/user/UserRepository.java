package app.SocialLibraryAPI.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT targetUser FROM UserEntity targetUser " +
            "JOIN targetUser.followers f " + //  utilizatorii care urmăresc targetUser
            "WHERE f.id IN :myFollowingIds " + // targetUser este urmărit de cineva pe care eu îl urmăresc
            "AND targetUser.id != :myId " + // nu mă recomand pe mine
            "AND targetUser.id NOT IN :myFollowingIds " + // nu recomand pe cineva pe care deja îl urmăresc
            "GROUP BY targetUser " +
            "ORDER BY COUNT(f) DESC")
    List<UserEntity> findMutualConnections(
            @Param("myFollowingIds") List<Long> myFollowingIds,
            @Param("myId") Long myId,
            Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(LOWER(u.fullName) LIKE LOWER(:search) " +
            "OR LOWER(u.email) LIKE LOWER(:search))")
    Page<UserEntity> searchUsers(@Param("search") String search, Pageable pageable);
}
