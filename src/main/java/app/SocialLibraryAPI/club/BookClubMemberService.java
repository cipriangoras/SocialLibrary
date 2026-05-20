package app.SocialLibraryAPI.club;

import app.SocialLibraryAPI.club.dto.BookClubMemberDTO;
import app.SocialLibraryAPI.user.Role;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
                        member.getJoinedAt(),
                        member.getClubRole().name()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void kickMember(String actionUserEmail, Integer clubId, Long userIdToKick) {
        log.info("User {} attempting to kick user id {} from club id {}", actionUserEmail, userIdToKick, clubId);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity actionUser = userRepository.findByEmail(actionUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Action user not found."));

        if (actionUser.getId().equals(userIdToKick)) {
            throw new IllegalStateException("You cannot remove yourself using this command. Use the 'Leave' endpoint instead.");
        }

        boolean isCreator = club.getUser().getId().equals(actionUser.getId());
        boolean isGlobalAdmin = actionUser.getRole() == Role.ADMIN;
        boolean isClubModerator = false;

        if (!isCreator && !isGlobalAdmin) {
            BookClubMembersIdEntity actionUserMembershipId = new BookClubMembersIdEntity(clubId, actionUser.getId());
            Optional<BookClubMembersEntity> actionUserMembership = membersRepository.findById(actionUserMembershipId);

            if (actionUserMembership.isPresent() && actionUserMembership.get().getClubRole() == ClubRole.MODERATOR) {
                isClubModerator = true;
            }
        }

        if (!isCreator && !isGlobalAdmin && !isClubModerator) {
            log.error("User {} is not authorized to kick members from club {}", actionUserEmail, clubId);
            throw new IllegalStateException("You don't have permission to remove members from this club.");
        }

        BookClubMembersIdEntity targetMembershipId = new BookClubMembersIdEntity(clubId, userIdToKick);
        BookClubMembersEntity targetMembership = membersRepository.findById(targetMembershipId)
                .orElseThrow(() -> new EntityNotFoundException("The target user is not a member of this club."));

        if (targetMembership.getUser().getId().equals(club.getUser().getId())) {
            throw new IllegalStateException("You cannot kick the creator of the club!");
        }
        club.setMemberCount(club.getMemberCount() - 1);

        membersRepository.delete(targetMembership);
        log.info("Successfully kicked user id {} from club id {}", userIdToKick, clubId);
    }

    @Transactional
    void changeClubRole(String actionUserEmail, Integer clubId, Long userIdToChangeRole, String role){
        log.info("User {} attempting to promote user id {} from club id {}", actionUserEmail, userIdToChangeRole, clubId);

        BookClubEntity club = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Book club not found."));

        UserEntity actionUser = userRepository.findByEmail(actionUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Action user not found."));

        boolean isCreator = club.getUser().getId().equals(actionUser.getId());
        boolean isGlobalAdmin = actionUser.getRole() == Role.ADMIN;


        BookClubMembersIdEntity targetMembershipId = new BookClubMembersIdEntity(clubId, userIdToChangeRole);
        BookClubMembersEntity targetMembership = membersRepository.findById(targetMembershipId)
                .orElseThrow(() -> new EntityNotFoundException("The target user is not a member of this club."));


        if (targetMembership.getUser().getId().equals(club.getUser().getId())) {
            throw new IllegalStateException("You cannot change the role of the club's creator!");
        }

        if(!isCreator && !isGlobalAdmin){
            log.error("User {} is not authorized to change members role from club {}", actionUserEmail, clubId);
            throw new IllegalStateException("You don't have permission to change members role.");
        }

        String upperRole = role.toUpperCase();
        if(!upperRole.equals(ClubRole.MEMBER.name()) && !upperRole.equals(ClubRole.MODERATOR.name())){
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        targetMembership.setClubRole(ClubRole.valueOf(upperRole));

        membersRepository.save(targetMembership);

    }
}