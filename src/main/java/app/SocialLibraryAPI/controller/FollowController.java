package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long id, Principal principal) {
        log.info("REST request to follow user id: {} by user: {}", id, principal.getName());
        followService.followUser(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long id, Principal principal) {
        log.info("REST request to unfollow user id: {} by user: {}", id, principal.getName());
        followService.unfollowUser(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable Long id) {
        log.info("REST request to fetch followers for user id: {}", id);
        return ResponseEntity.ok(followService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable Long id) {
        log.info("REST request to fetch following list for user id: {}", id);
        return ResponseEntity.ok(followService.getFollowing(id));
    }
}