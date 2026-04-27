package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.response.BookClubMemberDTO;
import app.SocialLibraryAPI.entity.*;
import app.SocialLibraryAPI.repository.BookClubMembersRepository;
import app.SocialLibraryAPI.repository.BookClubRepository;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookClubMemberService {

    private final BookClubMembersRepository membersRepository;
    private final BookClubRepository bookClubRepository;
    private final UserRepository userRepository;

    public BookClubMemberService(BookClubMembersRepository membersRepository, BookClubRepository bookClubRepository, UserRepository userRepository) {
        this.membersRepository = membersRepository;
        this.bookClubRepository = bookClubRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<BookClubMemberDTO> getClubMembers(Integer clubId) {
        log.info("Fetching members for book club id: {}", clubId);

        if (!bookClubRepository.existsById(clubId)) {
            throw new EntityNotFoundException("Book club not found.");
        }

        return membersRepository.findByBookClub_Id(clubId).stream()
                .map(member -> new BookClubMemberDTO(
                        member.getUser().getId(),
                        member.getUser().getFullName(),
                        member.getUser().getProfilePicUrl(),
                        member.getJoinedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void kickMember(String adminEmail, Integer clubId, Long userIdToKick) {
        log.info("User {} attempting to kick user id {} from club id {}", adminEmail, userIdToKick, clubId);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity actionUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found."));

        boolean isCreator = club.getUser().getId().equals(actionUser.getId());
        boolean isGlobalAdmin = actionUser.getRole() == Role.ADMIN;

        if (!isCreator && !isGlobalAdmin) {
            log.error("User {} is not authorized to kick members from club {}", adminEmail, clubId);
            throw new IllegalStateException("You don’t have permission to remove members from this club.");
        }

        if (actionUser.getId().equals(userIdToKick)) {
            throw new IllegalStateException("You cannot remove yourself using this command. Use the 'Leave' endpoint instead.");
        }

        BookClubMembersId membershipId = new BookClubMembersId(clubId, userIdToKick);
        BookClubMembersEntity membership = membersRepository.findById(membershipId)
                .orElseThrow(() -> new EntityNotFoundException("User is not a member of this club."));

        membersRepository.delete(membership);
        log.info("Successfully kicked user id {} from club id {}", userIdToKick, clubId);
    }
}