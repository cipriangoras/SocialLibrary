package app.SocialLibraryAPI.club.dto;

import java.time.LocalDateTime;

public record ClubSessionDTO(
        Integer id,
        String title,
        String description,
        LocalDateTime startTime,
        Integer bookClubId,
        boolean isActive
) {}