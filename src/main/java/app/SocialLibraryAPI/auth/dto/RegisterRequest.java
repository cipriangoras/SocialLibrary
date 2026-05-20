package app.SocialLibraryAPI.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    @NotBlank @Size(min=3, max=30)
    private String fullName;
    @NotBlank @Email @Size(max=254)
    private String email;
    @NotBlank @Size(min=8, max=72)
    private String password;
    @NotBlank @Size(min=8, max=72)
    private String checkPassword;
}