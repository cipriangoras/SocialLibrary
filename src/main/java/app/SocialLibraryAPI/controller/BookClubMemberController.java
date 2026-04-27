package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.response.BookClubMemberDTO;
import app.SocialLibraryAPI.service.BookClubMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/book-clubs/{clubId}/members")
public class BookClubMemberController {

    private final BookClubMemberService memberService;

    public BookClubMemberController(BookClubMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<BookClubMemberDTO>> getMembers(@PathVariable Integer clubId) {
        log.info("REST request to fetch members for club id: {}", clubId);
        return ResponseEntity.status(200).body(memberService.getClubMembers(clubId));
    }

    @DeleteMapping("/{userIdToKick}")
    public ResponseEntity<Void> kickMember(
            @PathVariable Integer clubId,
            @PathVariable Long userIdToKick,
            Principal principal) {
        log.info("REST request to kick user id: {} from club id: {} by user: {}", userIdToKick, clubId, principal.getName());
        memberService.kickMember(principal.getName(), clubId, userIdToKick);
        return ResponseEntity.status(200).build();
    }
}