package app.SocialLibraryAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;
    private String username;
    private int age;
    private String email;
    private String bio;
    private String profilePicUrl;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, int age, String email, String bio, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.email = email;
        this.bio = bio;
        this.profilePicUrl = profilePicUrl;
    }

}
