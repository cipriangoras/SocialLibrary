package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record FeedItemDTO(
        String type, // REVIEW sau ARTICLE
        Integer itemId,
        String authorName,
        String authorProfilePicUrl,
        String contentSnippet,
        Integer relatedBookId,
        String relatedBookTitle,
        String relatedBookCoverImageUrl,
        LocalDateTime createdAt
) {}