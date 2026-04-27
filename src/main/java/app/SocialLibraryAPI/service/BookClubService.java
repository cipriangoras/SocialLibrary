package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.CreateBookClubRequest;
import app.SocialLibraryAPI.dto.request.CreateClubSessionRequest;
import app.SocialLibraryAPI.dto.response.BookClubDTO;
import app.SocialLibraryAPI.dto.response.ClubSessionDTO;
import app.SocialLibraryAPI.entity.*;
import app.SocialLibraryAPI.mappers.BookClubMapper;
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

        BookClubEntity club = new BookClubEntity();
        club.setName(request.name());
        club.setDescription(request.description());
        club.setUser(creator);
        club.setBook(book);

        BookClubEntity savedClub = bookClubRepository.save(club);

        joinClub(userEmail, savedClub.getId());

        log.info("Successfully created book club id: {}", savedClub.getId());
        return BookClubMapper.toClubDTO(savedClub);
    }

    @Transactional
    public void joinClub(String userEmail, Integer clubId) {
        log.info("User {} is attempting to join club id: {}", userEmail, clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        BookClubMembersId membersId = new BookClubMembersId(clubId, user.getId());

        if (membersRepository.existsById(membersId)) {
            throw new IllegalStateException("You are already a member of this club!");
        }

        BookClubMembersEntity membership = new BookClubMembersEntity();
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

        BookClubMembersEntity membership = membersRepository.findById(membersId)
                .orElseThrow(() -> new EntityNotFoundException("You are not a member of this club."));

        membersRepository.delete(membership);
        log.info("User {} successfully left club {}", userEmail, clubId);
    }

    @Transactional
    public ClubSessionDTO addSession(String userEmail, Integer clubId, CreateClubSessionRequest request) {
        log.info("Attempting to add session '{}' to club id: {}", request.title(), clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCreator && !isAdmin) {
            log.error("User {} is not authorized to add sessions to club {}", userEmail, clubId);
            throw new IllegalStateException("You don't have permission to create sessions for this club. Only the creator or an Admin can do this.");
        }

        ClubSession session = new ClubSession();
        session.setBookClub(club);
        session.setTitle(request.title());
        session.setStartTime(request.startTime());

        ClubSession savedSession = sessionRepository.save(session);
        log.info("Successfully added session id: {}", savedSession.getId());

        return BookClubMapper.toSessionDTO(savedSession);
    }

    @Transactional(readOnly = true)
    public Page<BookClubDTO> getAllBookClubs(String bookTitle, Pageable pageable) {
        log.info("Fetching all book clubs with bookTitle filter: {}", bookTitle);

        return bookClubRepository.findWithFilters(bookTitle, pageable)
                .map(BookClubMapper::toClubDTO);
    }

    @Transactional
    public void deleteBookClub(String userEmail, Integer clubId) {
        log.info("Attempting to delete book club id: {} by user: {}", clubId, userEmail);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCreator && !isAdmin) {
            log.error("User {} is not authorized to delete club {}", userEmail, clubId);
            throw new IllegalStateException("You don't have permission to delete this club. Only the creator or an Admin can do this.");
        }

        bookClubRepository.delete(club);
        log.info("Successfully deleted book club id: {}", clubId);
    }


}