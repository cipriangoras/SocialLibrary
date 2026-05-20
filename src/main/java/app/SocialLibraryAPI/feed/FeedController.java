package app.SocialLibraryAPI.feed;

import app.SocialLibraryAPI.feed.dto.FeedItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<FeedItemDTO>> getMyFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "15") int limit,
            Principal principal) {

        log.info("REST request to get activity feed for user: {}", principal.getName());

        LocalDateTime actualCursor = (cursor != null) ? cursor : LocalDateTime.now();

        return ResponseEntity.status(200).body(feedService.getUserFeed(principal.getName(), actualCursor, limit));
    }
}