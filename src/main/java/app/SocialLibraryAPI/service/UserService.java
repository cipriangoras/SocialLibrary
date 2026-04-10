package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.User;
import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.entity.Role;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.mappers.UserMapper;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserDTO createUser(User userToCreate){

        var userEntity = UserMapper.toUserEntity(userToCreate);

        userEntity.setRole(Role.USER);

        //userEntity.setPassword(ENCRYPTED pass);
        if(userRepository.existsByEmail(userEntity.getEmail())){
            throw new IllegalStateException("Email already in use: " + userEntity.getEmail());
        }

        userRepository.save(userEntity);
        return UserMapper.toDTO(userEntity);
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(Long id){
        return userRepository.findById(id).
                map(UserMapper::toDTO).
                orElseThrow(() -> new EntityNotFoundException("Nu a fost gasit user ul cu id ul:" + id));
    }

    public void deleteUserById(Long id){
        UserEntity user = userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Nu a fost gasit user ul cu id ul: " + id));
        userRepository.deleteById(id);
    }

    public UserDTO updateUserById(Long id, User updatedUser) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nu a fost gasit user-ul cu id-ul: " + id));

        existingUser.setUsername(updatedUser.username());
        existingUser.setAge(updatedUser.age());
        //existingUser.setPassword(ENCRYPTED pass);
        existingUser.setEmail(updatedUser.email());
        existingUser.setBio(updatedUser.bio());
        existingUser.setProfilePicUrl(updatedUser.profilePicUrl());

        UserEntity savedUser = userRepository.save(existingUser);
        return UserMapper.toDTO(savedUser);
    }



}
