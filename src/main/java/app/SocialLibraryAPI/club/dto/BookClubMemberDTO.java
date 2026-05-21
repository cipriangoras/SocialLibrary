package app.SocialLibraryAPI.club.dto;

import app.SocialLibraryAPI.club.ClubRole;

import java.time.LocalDateTime;

public record BookClubMemberDTO(
        Long userId,
        String fullName,
        String profilePicUrl,
        LocalDateTime joinedAt,
        String clubRole
) {}