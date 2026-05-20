package app.SocialLibraryAPI.club.dto;

import java.time.LocalDateTime;

public record ClubSessionDTO(
        Integer id,
        String title,
        LocalDateTime startTime,
        Integer bookClubId
) {}