package app.SocialLibraryAPI.library.dto;

import app.SocialLibraryAPI.library.Status;

public record UpdateLibraryRequest(
        Status status,
        boolean isFavorite
) {}