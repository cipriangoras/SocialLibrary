package app.SocialLibraryAPI.feed.dto;

import app.SocialLibraryAPI.feed.FeedType;

import java.time.LocalDateTime;

public record FeedItemDTO(
        Integer itemId,
        FeedType type,
        String authorName,
        String authorProfilePicUrl,
        String title,
        String content,
        Integer relatedBookId,
        String relatedBookTitle,
        String relatedBookCoverImageUrl,
        Integer rating,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {}