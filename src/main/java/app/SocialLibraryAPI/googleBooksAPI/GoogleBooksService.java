package app.SocialLibraryAPI.googleBooksAPI;

import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.book.BookMapper;
import app.SocialLibraryAPI.book.BookRepository;
import app.SocialLibraryAPI.book.dto.BookDTO;
import app.SocialLibraryAPI.genre.GenreEntity;
import app.SocialLibraryAPI.genre.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleBooksService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final RestClient restClient;

    @Value("${google.books.api.key:}")
    private String apiKey;

    public GoogleBooksService(BookRepository bookRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }

    @Transactional
    public BookDTO importBookByTitle(String title) {
        log.info("Searching Google Books for title: {}", title);

        String uri = "/volumes?q=intitle:{title}&maxResults=1";
        if (apiKey != null && !apiKey.isEmpty()) {
            uri += "&key=" + apiKey;
        }

        GoogleBooksResponse response = restClient.get()
                .uri(uri, title)
                .retrieve()
                .body(GoogleBooksResponse.class);

        if (response == null || response.items() == null || response.items().isEmpty()) {
            throw new RuntimeException("No book found on Google Books with title: " + title);
        }

        var volumeInfo = response.items().get(0).volumeInfo();

        String isbn = "UNKNOWN-" + System.currentTimeMillis();
        if (volumeInfo.industryIdentifiers() != null) {
            isbn = volumeInfo.industryIdentifiers().stream()
                    .filter(id -> "ISBN_13".equals(id.type()))
                    .map(id -> id.identifier())
                    .findFirst()
                    .orElse(volumeInfo.industryIdentifiers().get(0).identifier());
        }

        if (bookRepository.existsByIsbn(isbn)) {
            log.info("Book with ISBN {} already exists locally.", isbn);
            BookEntity existingBook = bookRepository.findByIsbn(isbn).orElseThrow();
            return BookMapper.toDTO(existingBook);
        }

        BookEntity book = new BookEntity();
        book.setTitle(volumeInfo.title());

        String author = (volumeInfo.authors() != null && !volumeInfo.authors().isEmpty())
                ? volumeInfo.authors().get(0) : "Unknown Author";
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setDescription(volumeInfo.description() != null ? volumeInfo.description() : "No description available.");
        book.setPublicationYear(LocalDate.now());

        if (volumeInfo.imageLinks() != null && volumeInfo.imageLinks().thumbnail() != null) {
            book.setCoverImageUrl(volumeInfo.imageLinks().thumbnail().replace("http://", "https://"));
        }

        Set<GenreEntity> mappedGenres = new HashSet<>();

        if (volumeInfo.categories() != null) {
            for (String category : volumeInfo.categories()) {
                String[] splitCategories = category.split("/");

                for (String rawGenre : splitCategories) {
                    String formattedGenreName = capitalizeWords(rawGenre.trim());

                    if (formattedGenreName.isEmpty()) continue;

                    GenreEntity genre = genreRepository.findByNameIgnoreCase(formattedGenreName)
                            .orElseGet(() -> {
                                log.info("Genre '{}' not found. Creating it dynamically...", formattedGenreName);
                                GenreEntity newGenre = new GenreEntity();
                                newGenre.setName(formattedGenreName);
                                return genreRepository.save(newGenre);
                            });

                    mappedGenres.add(genre);
                }
            }
        }

        if (mappedGenres.isEmpty()) {
            GenreEntity defaultGenre = genreRepository.findByNameIgnoreCase("General")
                    .orElseGet(() -> {
                        GenreEntity g = new GenreEntity();
                        g.setName("General");
                        return genreRepository.save(g);
                    });
            mappedGenres.add(defaultGenre);
        }

        book.setGenres(mappedGenres);
        book.setRating(0.0f);

        BookEntity savedBook = bookRepository.save(book);
        log.info("Successfully imported '{}' with {} genres.", savedBook.getTitle(), mappedGenres.size());

        return BookMapper.toDTO(savedBook);
    }


    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        return Arrays.stream(str.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}