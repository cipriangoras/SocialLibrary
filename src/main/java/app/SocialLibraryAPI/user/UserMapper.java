package app.SocialLibraryAPI.user;

import app.SocialLibraryAPI.user.dto.UserDTO;
import app.SocialLibraryAPI.user.dto.User;

public class UserMapper {
    public static UserDTO toDTO(UserEntity user){
        if(user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getAge(),
                user.getEmail(),
                user.getBio(),
                user.getProfilePicUrl(),
                user.getRole().name()
        );
    }

    public static UserEntity toEntity(User user){
        if(user == null){
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(user.fullName());
        userEntity.setAge(user.age());
        userEntity.setEmail(user.email());
        userEntity.setPassword(user.password());
        userEntity.setBio(user.bio());
        userEntity.setProfilePicUrl(user.profilePicUrl());

        return userEntity;
    }

}
