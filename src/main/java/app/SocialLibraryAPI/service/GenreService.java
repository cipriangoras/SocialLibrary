package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CreateGenreRequest;
import app.SocialLibraryAPI.dto.response.GenreDTO;
import app.SocialLibraryAPI.entity.GenreEntity;
import app.SocialLibraryAPI.mappers.GenreMapper;
import app.SocialLibraryAPI.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public GenreDTO createGenre(CreateGenreRequest request) {
        log.info("Attempting to create genre: {}", request.name());
        if (genreRepository.existsByNameIgnoreCase(request.name())) {
            log.error("Failed to create genre. Genre already exists: {}", request.name());
            throw new IllegalStateException("Genre already exists: " + request.name());
        }

        GenreEntity genre = new GenreEntity();
        genre.setName(request.name().trim());

        GenreEntity saved = genreRepository.save(genre);
        log.info("Successfully created genre '{}' with ID: {}", saved.getName(), saved.getId());
        return GenreMapper.toDTO(saved);
    }

    public List<GenreDTO> getAllGenres() {
        log.info("Fetching all genres");
        List<GenreDTO> genres = genreRepository.findAll().stream().map(GenreMapper::toDTO).toList();
        log.info("Found {} genres", genres.size());
        return genres;
    }

    public GenreDTO getGenreById(Integer id) {
        log.info("Fetching genre with id: {}", id);
        return genreRepository.findById(id).map(GenreMapper::toDTO)
                .orElseThrow(() -> {
                    log.error("Genre not found with id: {}", id);
                    return new EntityNotFoundException("Genre not found. Id: " + id);
                });
    }


    public void deleteGenreById(Integer id) {
        log.info("Attempting to delete genre with id: {}", id);
        GenreEntity genre = genreRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete. Genre not found with id: {}", id);
                    return new EntityNotFoundException("Genre not found. Id: " + id);
                });

        genreRepository.delete(genre);
        log.info("Successfully deleted genre with id: {}", id);
    }
}
