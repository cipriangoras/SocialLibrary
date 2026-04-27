package app.SocialLibraryAPI.dto.response;

public record BookClubDTO(
        Integer id,
        String name,
        String description,
        Long creatorId,
        String creatorName,
        Integer bookId,
        String bookTitle
) {}