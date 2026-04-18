package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.request.User;
import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.entity.Role;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.mappers.UserMapper;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDTO createUser(User userToCreate){
        log.info("Attempting to create user with email: {}", userToCreate.email());

        var userEntity = UserMapper.toUserEntity(userToCreate);

        userEntity.setRole(Role.USER);
        userEntity.setPassword(passwordEncoder.encode(userToCreate.password()));
        if(userRepository.existsByEmail(userEntity.getEmail())){
            log.error("Failed to create user. Email already in use: {}", userEntity.getEmail());
            throw new IllegalStateException("Email already in use: " + userEntity.getEmail());
        }

        userRepository.save(userEntity);
        log.info("Successfully created user with email: {}", userEntity.getEmail());
        return UserMapper.toDTO(userEntity);
    }

    public List<UserDTO> getAllUsers(){
        log.info("Fetching all users");
        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} users", users.size());
        return users;
    }

    public UserDTO findUserById(Long id){
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id).
                map(UserMapper::toDTO).
                orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });
    }

    public void deleteUserById(Long id){
        log.info("Attempting to delete user with id: {}", id);
        UserEntity user = userRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Failed to delete. User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });
        userRepository.deleteById(id);
        log.info("Successfully deleted user with id: {}", id);
    }

    public UserDTO updateUserById(Long id, User updatedUser) {
        log.info("Attempting to update user with id: {}", id);
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to update. User not found with id: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });

        existingUser.setFullName(updatedUser.fullName());
        existingUser.setAge(updatedUser.age());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.password()));
        existingUser.setEmail(updatedUser.email());
        existingUser.setBio(updatedUser.bio());
        existingUser.setProfilePicUrl(updatedUser.profilePicUrl());

        UserEntity savedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with id: {}", id);
        return UserMapper.toDTO(savedUser);
    }


    public UserDTO findUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
    }
}
