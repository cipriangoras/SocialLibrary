package app.SocialLibraryAPI.seeder;

import app.SocialLibraryAPI.article.ArticleEntity;
import app.SocialLibraryAPI.article.ArticleRepository;
import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.book.BookRepository;
import app.SocialLibraryAPI.club.*;
import app.SocialLibraryAPI.genre.GenreEntity;
import app.SocialLibraryAPI.genre.GenreRepository;
import app.SocialLibraryAPI.library.Status;
import app.SocialLibraryAPI.library.UserBookLibraryEntity;
import app.SocialLibraryAPI.library.UserBookLibraryRepository;
import app.SocialLibraryAPI.review.ReviewEntity;
import app.SocialLibraryAPI.review.ReviewRepository;
import app.SocialLibraryAPI.user.Role;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.user.UserRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ArticleRepository articleRepository;
    private final BookClubRepository bookClubRepository;
    private final UserBookLibraryRepository libraryRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final BookClubMembersRepository bookClubMemberRepository;
    private final ReviewRepository reviewRepository;

    public DatabaseSeeder(UserRepository userRepository, BookRepository bookRepository,
                          ArticleRepository articleRepository, BookClubRepository bookClubRepository,
                          UserBookLibraryRepository libraryRepository, PasswordEncoder passwordEncoder,
                          GenreRepository genreRepository, BookClubMembersRepository bookClubMemberRepository,
                          ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.articleRepository = articleRepository;
        this.bookClubRepository = bookClubRepository;
        this.libraryRepository = libraryRepository;
        this.passwordEncoder = passwordEncoder;
        this.genreRepository = genreRepository;
        this.bookClubMemberRepository = bookClubMemberRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- 0. CREARE ADMIN (Prevenire Race Conditions) ---
        String adminEmail = "admin@local";
        if (!userRepository.existsByEmail(adminEmail)) {
            UserEntity admin = new UserEntity();
            admin.setFullName("Administrator");
            admin.setEmail(adminEmail);
            admin.setAge(30);
            admin.setPassword(passwordEncoder.encode("admin123")); // Parola admin
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Contul de ADMIN a fost creat.");
        }

        // Dacă avem mai mult de 1 utilizator (Adminul), oprim seeding-ul pentru a nu duplica datele
        if (userRepository.count() > 1) {
            System.out.println("Baza de date conține deja date de test. Seeding-ul a fost anulat.");
            return;
        }

        System.out.println("Începe procesul de Seeding 100% local...");
        Faker faker = new Faker();
        Random random = new Random();

        // --- 0.5 GENERARE GENURI LITERARE ---
        List<GenreEntity> genres = new ArrayList<>();
        String[] genreNames = {"Ficțiune", "Science Fiction", "Dramă", "Thriller", "Romance", "Istorie", "Filozofie", "Realism Magic", "Biografie"};
        for (String name : genreNames) {
            GenreEntity genre = new GenreEntity();
            genre.setName(name);
            genres.add(genre);
        }
        genres = genreRepository.saveAll(genres);

        // --- 1. GENERARE CĂRȚI MOCK ---
        List<BookEntity> books = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            BookEntity book = new BookEntity();
            book.setTitle(faker.book().title());
            book.setAuthor(faker.book().author());
            book.setDescription(faker.lorem().paragraph(3));

            // FIX COPERTĂ: Un serviciu mult mai stabil (placehold.co)
            book.setCoverImageUrl("https://placehold.co/300x400/2c3e50/ffffff.png?text=Carte+" + (i + 1));
            book.setIsbn(faker.code().isbn13());

            // FIX RATING: Generează un număr între 1.0 și 5.0, cu o singură zecimală
            double randomRating = 1.0 + (4.0 * random.nextDouble());
            book.setRating((float) (Math.round(randomRating * 10.0) / 10.0));

            // Atribuie 1 sau 2 genuri aleatorii fiecărei cărți
            int numGenres = random.nextInt(2) + 1;
            for(int j = 0; j < numGenres; j++) {
                book.getGenres().add(genres.get(random.nextInt(genres.size())));
            }
            books.add(book);
        }
        books = bookRepository.saveAll(books);
        System.out.println("S-au generat 20 de cărți de test cu genuri și rating.");

        // --- 2. GENERARE UTILIZATORI ---
        List<UserEntity> users = new ArrayList<>();
        String commonPasswordHash = passwordEncoder.encode("password123"); // Parolă de minim 8 caractere

        for (int i = 0; i < 40; i++) {
            UserEntity user = new UserEntity();
            user.setFullName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(commonPasswordHash);
            user.setBio(faker.lorem().sentence(10));
            user.setAge(faker.number().numberBetween(18, 65));
            user.setProfilePicUrl("https://ui-avatars.com/api/?name=" + user.getFullName().replace(" ", "+"));
            user.setRole(Role.USER);
            users.add(user);
        }
        users = userRepository.saveAll(users);
        System.out.println("S-au generat 40 de utilizatori.");

        // --- 3. GENERARE SOCIAL GRAPH (FOLLOWERS) ---
        for (UserEntity user : users) {
            int followersCount = random.nextInt(8) + 2;
            for (int j = 0; j < followersCount; j++) {
                UserEntity followedUser = users.get(random.nextInt(users.size()));
                if (!user.getId().equals(followedUser.getId())) {
                    user.getFollowing().add(followedUser);
                }
            }
        }
        userRepository.saveAll(users);

        // --- 4. GENERARE BIBLIOTECĂ VIRTUALĂ ---
        for (UserEntity user : users) {
            int booksInLibrary = random.nextInt(6) + 2;
            for (int j = 0; j < booksInLibrary; j++) {
                BookEntity randomBook = books.get(random.nextInt(books.size()));

                UserBookLibraryEntity entry = new UserBookLibraryEntity();
                entry.setUser(user);
                entry.setBook(randomBook);
                entry.setStatus(Status.values()[ThreadLocalRandom.current().nextInt(Status.values().length)]);
                entry.setFavorite(random.nextBoolean());

                libraryRepository.save(entry);
            }
        }

        // --- 5. GENERARE ARTICOLE ---
        List<ArticleEntity> articles = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            ArticleEntity article = new ArticleEntity();
            article.setTitle(faker.book().title() + " - O analiză profundă");
            article.setContent(faker.lorem().paragraphs(4).stream().collect(Collectors.joining("\n\n")));
            article.setAuthor(users.get(random.nextInt(users.size())));
            article.setRelatedBook(books.get(random.nextInt(books.size())));
            article.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24)));

            articles.add(article);
        }
        articleRepository.saveAll(articles);

        // --- 6. GENERARE BOOK CLUBS ȘI MEMBRI ---
        for (int i = 0; i < 10; i++) {
            BookClubEntity club = new BookClubEntity();
            club.setName("Clubul: " + faker.book().genre() + " " + faker.color().name());
            club.setDescription(faker.lorem().sentence(15));
            club.setClubGuidelines("1. Fii respectuos.\n2. Citește cartea la timp.");
            club.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(60)));

            UserEntity creator = users.get(random.nextInt(users.size()));
            club.setUser(creator);
            club.setBook(books.get(random.nextInt(books.size())));

            club = bookClubRepository.save(club); // Aici clubul primește ID-ul generat

            // --- Adăugăm creatorul ca ADMIN ---
            BookClubMembersEntity ownerMember = new BookClubMembersEntity();

            app.SocialLibraryAPI.club.BookClubMembersIdEntity ownerId = new app.SocialLibraryAPI.club.BookClubMembersIdEntity();
            ownerId.setClubId(club.getId());
            ownerId.setUserId(creator.getId());
            ownerMember.setId(ownerId);

            ownerMember.setBookClub(club);
            ownerMember.setUser(creator);
            ownerMember.setJoinedAt(club.getCreatedAt());
            bookClubMemberRepository.save(ownerMember);

            // --- Adăugăm alți membri aleatorii ---
            int membersCount = random.nextInt(8) + 3;
            for (int j = 0; j < membersCount; j++) {
                UserEntity randomMember = users.get(random.nextInt(users.size()));

                if (!randomMember.getId().equals(creator.getId())) {
                    BookClubMembersEntity member = new BookClubMembersEntity();

                    app.SocialLibraryAPI.club.BookClubMembersIdEntity memberId = new app.SocialLibraryAPI.club.BookClubMembersIdEntity();
                    memberId.setClubId(club.getId());
                    memberId.setUserId(randomMember.getId());
                    member.setId(memberId);

                    member.setBookClub(club);
                    member.setUser(randomMember);
                    member.setClubRole(ClubRole.MEMBER);
                    member.setJoinedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
                    bookClubMemberRepository.save(member);
                }
            }
        }
        System.out.println("S-au generat 10 cluburi de carte împreună cu membrii asociați.");

        // --- 7. GENERARE RECENZII ---
        List<ReviewEntity> reviews = new ArrayList<>();
        for (BookEntity book : books) {
            int reviewsCount = random.nextInt(5) + 2;
            for (int j = 0; j < reviewsCount; j++) {
                ReviewEntity review = new ReviewEntity();
                review.setBook(book);
                review.setUser(users.get(random.nextInt(users.size())));
                review.setRating(random.nextInt(5) + 1);
                review.setContent(faker.lorem().paragraph(2));
                review.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(100)));

                reviews.add(review);
            }
        }
        reviewRepository.saveAll(reviews);
        System.out.println("S-au generat " + reviews.size() + " recenzii pentru cărți.");

        System.out.println("Seeding finalizat cu succes!");
    }
}