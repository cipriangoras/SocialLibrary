package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record ClubSessionDTO(
        Integer id,
        String title,
        LocalDateTime startTime,
        Integer bookClubId
) {}