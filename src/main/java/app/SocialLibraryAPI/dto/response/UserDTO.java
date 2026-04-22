package app.SocialLibraryAPI.dto.response;

public record UserDTO(
        Long id,
        String fullName,
        int age,
        String email,
        String bio,
        String profilePicUrl,
        String role 
) {}