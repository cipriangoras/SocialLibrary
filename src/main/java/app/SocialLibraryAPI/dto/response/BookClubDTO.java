package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookClubDTO(
        Integer id,
        String name,
        String description,
        String clubGuidelines,
        LocalDateTime createdAt,
        Long creatorId,
        String creatorName,
        Integer currentBookId,
        String currentBookTitle,

        // Date calculate
        int booksCompleted,
        int discussionsCount,
        float avgAttendance,
        List<String> previousReads
) {}