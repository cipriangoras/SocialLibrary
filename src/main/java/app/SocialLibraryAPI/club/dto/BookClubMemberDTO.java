package app.SocialLibraryAPI.club.dto;

import java.time.LocalDateTime;

public record BookClubMemberDTO(
        Long userId,
        String fullName,
        String profilePicUrl,
        LocalDateTime joinedAt
) {}