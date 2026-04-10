package app.SocialLibraryAPI.dto.request;

import app.SocialLibraryAPI.entity.Role;
import jakarta.validation.constraints.*;

public record User (

    @NotBlank @Size(min=3, max=30)
    //@Patern ...
    String username,

    @Min(0) @Max(120)
    int age,

    @NotBlank @Email @Size(max=254)
    String email,

    @NotBlank @Size(min=8, max=72)
    //@Patern ...
    String password,

    @Size(max=500)
    String bio,

    @Size(max=2048)
    String profilePicUrl
){}
