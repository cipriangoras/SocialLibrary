package app.SocialLibraryAPI.user.dto;

public record UserDTO(
        Long id,
        String fullName,
        int age,
        String email,
        String bio,
        String profilePicUrl,
        String role 
) {}