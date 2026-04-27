package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CreateBookClubRequest;
import app.SocialLibraryAPI.dto.request.CreateClubSessionRequest;
import app.SocialLibraryAPI.dto.response.BookClubDTO;
import app.SocialLibraryAPI.dto.response.ClubSessionDTO;
import app.SocialLibraryAPI.entity.*;
import app.SocialLibraryAPI.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class BookClubService {

    private final BookClubRepository bookClubRepository;
    private final BookClubMembersRepository membersRepository;
    private final ClubSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookClubService(BookClubRepository bookClubRepository, BookClubMembersRepository membersRepository, ClubSessionRepository sessionRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.bookClubRepository = bookClubRepository;
        this.membersRepository = membersRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookClubDTO createClub(String userEmail, CreateBookClubRequest request) {
        log.info("Attempting to create book club '{}' by user: {}", request.name(), userEmail);

        UserEntity creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        BookEntity book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        BookClub club = new BookClub();
        club.setName(request.name());
        club.setDescription(request.description());
        club.setUser(creator);
        club.setBook(book);

        BookClub savedClub = bookClubRepository.save(club);

        joinClub(userEmail, savedClub.getId());

        log.info("Successfully created book club id: {}", savedClub.getId());
        return mapToClubDTO(savedClub);
    }

    @Transactional
    public void joinClub(String userEmail, Integer clubId) {
        log.info("User {} is attempting to join club id: {}", userEmail, clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        BookClub club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        BookClubMembersId membersId = new BookClubMembersId(clubId, user.getId());

        if (membersRepository.existsById(membersId)) {
            throw new IllegalStateException("Ești deja membru al acestui club!");
        }

        Book_Club_Members membership = new Book_Club_Members();
        membership.setId(membersId);
        membership.setBookClub(club);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        membersRepository.save(membership);
        log.info("User {} successfully joined club {}", userEmail, clubId);
    }

    @Transactional
    public void leaveClub(String userEmail, Integer clubId) {
        log.info("User {} is attempting to leave club id: {}", userEmail, clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        BookClubMembersId membersId = new BookClubMembersId(clubId, user.getId());

        Book_Club_Members membership = membersRepository.findById(membersId)
                .orElseThrow(() -> new EntityNotFoundException("Nu ești membru al acestui club."));

        membersRepository.delete(membership);
        log.info("User {} successfully left club {}", userEmail, clubId);
    }

    @Transactional
    public ClubSessionDTO addSession(String userEmail, Integer clubId, CreateClubSessionRequest request) {
        log.info("Attempting to add session '{}' to club id: {}", request.title(), clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        BookClub club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCreator && !isAdmin) {
            log.error("User {} is not authorized to add sessions to club {}", userEmail, clubId);
            throw new IllegalStateException("Nu ai permisiunea de a crea sesiuni pentru acest club. Doar creatorul sau un Admin poate face asta.");
        }

        ClubSession session = new ClubSession();
        session.setBookClub(club);
        session.setTitle(request.title());
        session.setStartTime(request.startTime());

        ClubSession savedSession = sessionRepository.save(session);
        log.info("Successfully added session id: {}", savedSession.getId());

        return mapToSessionDTO(savedSession);
    }

    @Transactional(readOnly = true)
    public Page<BookClubDTO> getAllBookClubs(String bookTitle, Pageable pageable) {
        log.info("Fetching all book clubs with bookTitle filter: {}", bookTitle);

        return bookClubRepository.findWithFilters(bookTitle, pageable)
                .map(this::mapToClubDTO);
    }

    @Transactional
    public void deleteBookClub(String userEmail, Integer clubId) {
        log.info("Attempting to delete book club id: {} by user: {}", clubId, userEmail);

        BookClub club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCreator && !isAdmin) {
            log.error("User {} is not authorized to delete club {}", userEmail, clubId);
            throw new IllegalStateException("Nu ai permisiunea de a șterge acest club. Doar creatorul sau un Admin pot face asta.");
        }

        bookClubRepository.delete(club);
        log.info("Successfully deleted book club id: {}", clubId);
    }

    private BookClubDTO mapToClubDTO(BookClub club) {
        return new BookClubDTO(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getUser().getId(),
                club.getUser().getFullName(),
                club.getBook().getId(),
                club.getBook().getTitle()
        );
    }

    private ClubSessionDTO mapToSessionDTO(ClubSession session) {
        return new ClubSessionDTO(
                session.getId(),
                session.getTitle(),
                session.getStartTime(),
                session.getBookClub().getId()
        );
    }
}