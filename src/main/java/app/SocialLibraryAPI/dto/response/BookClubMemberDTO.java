package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record BookClubMemberDTO(
        Long userId,
        String fullName,
        String profilePicUrl,
        LocalDateTime joinedAt
) {}