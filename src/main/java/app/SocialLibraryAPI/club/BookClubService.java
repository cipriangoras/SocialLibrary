package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.book.BookEntity;
import app.SocialLibraryAPI.book.BookRepository;
import app.SocialLibraryAPI.club.dto.CreateBookClubRequest;
import app.SocialLibraryAPI.club.dto.CreateClubSessionRequest;
import app.SocialLibraryAPI.club.dto.BookClubDTO;
import app.SocialLibraryAPI.club.dto.ClubSessionDTO;
import app.SocialLibraryAPI.user.Role;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookClubService {

    private final BookClubRepository bookClubRepository;
    private final BookClubMembersRepository membersRepository;
    private final ClubSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ChatMessageRepository chatMessageRepository;

    public BookClubService(BookClubRepository bookClubRepository, BookClubMembersRepository membersRepository, ClubSessionRepository sessionRepository, UserRepository userRepository, BookRepository bookRepository, ChatMessageRepository chatMessageRepository) {
        this.bookClubRepository = bookClubRepository;
        this.membersRepository = membersRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.chatMessageRepository = chatMessageRepository;
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
        club.setClubGuidelines(request.clubGuidelines());
        club.setCreatedAt(LocalDateTime.now());
        club.setUser(creator);
        club.setBook(book);
        club.setAvgAttendance(0.0f);

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

        BookClubMembersIdEntity membersId = new BookClubMembersIdEntity(clubId, user.getId());

        if (membersRepository.existsById(membersId)) {
            throw new IllegalStateException("You are already a member of this club!");
        }

        BookClubMembersEntity membership = new BookClubMembersEntity();
        membership.setId(membersId);
        membership.setBookClub(club);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        club.setMemberCount(club.getMemberCount() + 1);
        membersRepository.save(membership);
        log.info("User {} successfully joined club {}", userEmail, clubId);
    }

    @Transactional
    public void leaveClub(String userEmail, Integer clubId) {
        log.info("User {} is attempting to leave club id: {}", userEmail, clubId);

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        BookClubMembersIdEntity membersId = new BookClubMembersIdEntity(clubId, user.getId());

        BookClubMembersEntity membership = membersRepository.findById(membersId)
                .orElseThrow(() -> new EntityNotFoundException("You are not a member of this club."));

        club.setMemberCount(club.getMemberCount() - 1);

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

        boolean isClubModerator = false;

        BookClubMembersIdEntity actionUserMembershipId = new BookClubMembersIdEntity(clubId, user.getId());
        Optional<BookClubMembersEntity> actionUserMembership = membersRepository.findById(actionUserMembershipId);

        if (actionUserMembership.isPresent() && actionUserMembership.get().getClubRole() == ClubRole.MODERATOR) {
            isClubModerator = true;
        }

        if (!isCreator && !isAdmin && !isClubModerator) {
            log.error("User {} is not authorized to add sessions to club {}", userEmail, clubId);
            throw new IllegalStateException("You don't have permission to create sessions for this club. Only the creator, an Admin, or a Moderator can do this.");
        }

        ClubSessionEntity session = new ClubSessionEntity();
        session.setBookClub(club);
        session.setTitle(request.title());
        session.setStartTime(request.startTime());

        ClubSessionEntity savedSession = sessionRepository.save(session);
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

    @Transactional(readOnly = true)
    public BookClubDTO getBookClubById(Integer clubId) {
        log.info("Fetching book club details for id: {}", clubId);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found with id: " + clubId));

        return BookClubMapper.toClubDTO(club);
    }

    @Transactional
    public BookClubDTO changeCurrentBook(String userEmail, Integer clubId, Integer newBookId) {
        log.info("Attempting to change current book to {} for club id: {} by user: {}", newBookId, clubId, userEmail);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isClubModerator = false;

        BookClubMembersIdEntity actionUserMembershipId = new BookClubMembersIdEntity(clubId, user.getId());
        Optional<BookClubMembersEntity> actionUserMembership = membersRepository.findById(actionUserMembershipId);

        if (actionUserMembership.isPresent() && actionUserMembership.get().getClubRole() == ClubRole.MODERATOR) {
            isClubModerator = true;
        }
        if (!isCreator && !isAdmin && !isClubModerator) {
            log.error("User {} is not authorized to change the book for club {}", userEmail, clubId);
            throw new IllegalStateException("Only the club creator, an Admin or an Moderator can change the current book.");
        }

        BookEntity newBook = bookRepository.findById(newBookId)
                .orElseThrow(() -> new EntityNotFoundException("The new book was not found."));

        if (club.getBook() != null) {
            club.getPastBooks().add(club.getBook());
        }

        club.setBook(newBook);
        BookClubEntity updatedClub = bookClubRepository.save(club);

        log.info("Successfully changed book for club id: {}", clubId);
        return BookClubMapper.toClubDTO(updatedClub);
    }

    @Transactional(readOnly = true)
    public List<ClubSessionDTO> getClubSessions(Integer clubId) {
        log.info("Fetching sessions for book club id: {}", clubId);

        if (!bookClubRepository.existsById(clubId)) {
            throw new EntityNotFoundException("Book club not found.");
        }

        return sessionRepository.findByBookClub_IdOrderByStartTimeAsc(clubId).stream()
                .map(BookClubMapper::toSessionDTO)
                .toList();
    }

    @Transactional
    public ClubSessionDTO closeSession(String userEmail, Integer clubId, Integer sessionId) {
        log.info("User {} attempting to close session id: {} from club id: {}", userEmail, sessionId, clubId);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        ClubSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found."));

        if (!session.getBookClub().getId().equals(clubId)) {
            throw new IllegalArgumentException("Session does not belong to this book club.");
        }

        if (!session.isActive()) {
            throw new IllegalStateException("This session is already closed.");
        }

        UserEntity user = userRepository.findByEmail(userEmail).orElseThrow();
        boolean isCreator = club.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isClubModerator = false;

        BookClubMembersIdEntity actionUserMembershipId = new BookClubMembersIdEntity(clubId, user.getId());
        Optional<BookClubMembersEntity> actionUserMembership = membersRepository.findById(actionUserMembershipId);

        if (actionUserMembership.isPresent() && actionUserMembership.get().getClubRole() == ClubRole.MODERATOR) {
            isClubModerator = true;
        }

        if (!isCreator && !isAdmin && !isClubModerator) {
            throw new IllegalStateException("You don't have permission to close sessions for this club.");
        }

        session.setActive(false);
        sessionRepository.save(session);

        recalculateClubAttendance(club, session);

        log.info("Successfully closed session id: {}", sessionId);
        return BookClubMapper.toSessionDTO(session);
    }

    private void recalculateClubAttendance(BookClubEntity club, ClubSessionEntity session) {
        int totalMembers = club.getMembers() != null ? club.getMembers().size() : 0;
        if (totalMembers == 0) {
            return;
        }

        long uniqueParticipants = chatMessageRepository.countUniqueParticipantsBySessionId(session.getId());

        float currentSessionAttendance = ((float) uniqueParticipants / totalMembers) * 100.0f;

        long closedSessionsCount = sessionRepository.countByBookClub_IdAndIsActiveFalse(club.getId());

        if (closedSessionsCount <= 1) {
            club.setAvgAttendance(Math.round(currentSessionAttendance * 10.0f) / 10.0f);
        } else {
            float newAvg = ((club.getAvgAttendance() * (closedSessionsCount - 1)) + currentSessionAttendance) / closedSessionsCount;
            club.setAvgAttendance(Math.round(newAvg * 10.0f) / 10.0f);
        }

        bookClubRepository.save(club);
        log.debug("Recalculated attendance for club id: {}. New average: {}%", club.getId(), club.getAvgAttendance());
    }


}