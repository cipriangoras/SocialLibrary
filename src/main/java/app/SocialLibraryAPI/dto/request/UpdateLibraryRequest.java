package app.SocialLibraryAPI.dto.request;
import app.SocialLibraryAPI.entity.Status;

public record UpdateLibraryRequest(
        Status status,
        boolean isFavorite
) {}