package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.CreateGenreRequest;
import app.SocialLibraryAPI.dto.response.GenreDTO;
import app.SocialLibraryAPI.service.GenreService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/management/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody @Valid CreateGenreRequest request) {
        log.info("REST request to create genre: {}", request.name());
        return ResponseEntity.status(201).body(genreService.createGenre(request));
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        log.info("REST request to fetch all genres");
        return ResponseEntity.status(200).body(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Integer id) {
        log.info("REST request to fetch genre with id: {}", id);
        return ResponseEntity.status(200).body(genreService.getGenreById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenreById(@PathVariable Integer id){
        log.info("REST request to delete genre with id: {}", id);
        genreService.deleteGenreById(id);
        return ResponseEntity.status(200).build();
    }

}
