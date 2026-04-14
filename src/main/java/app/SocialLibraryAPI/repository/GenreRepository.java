package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

	boolean existsByNameIgnoreCase(String name);

	Optional<GenreEntity> findByNameIgnoreCase(String name);
}
