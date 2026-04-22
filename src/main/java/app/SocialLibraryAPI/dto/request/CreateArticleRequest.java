package app.SocialLibraryAPI.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        Integer relatedBookId
) {}