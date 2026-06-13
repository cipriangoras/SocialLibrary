package app.SocialLibraryAPI.user;

import app.SocialLibraryAPI.user.dto.UpdateProfileRequest;
import app.SocialLibraryAPI.user.dto.UserDTO;
import app.SocialLibraryAPI.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDTO createUser(User userToCreate){
        log.info("Attempting to create user with email: {}", userToCreate.email());

        var userEntity = UserMapper.toEntity(userToCreate);

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

    @Transactional
    public UserDTO updateMyProfile(String email, UpdateProfileRequest request) {
        log.info("Attempting to update profile for user: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Failed to update profile. User not found: {}", email);
                    return new EntityNotFoundException("User not found");
                });

        user.setFullName(request.fullName());
        user.setAge(request.age());
        user.setBio(request.bio());
        user.setProfilePicUrl(request.profilePicUrl());

        UserEntity savedUser = userRepository.save(user);
        log.info("Successfully updated profile for user: {}", email);

        return UserMapper.toDTO(savedUser);
    }
    public Page<UserDTO> searchUsers(String search, Pageable pageable) {
        log.info("Searching users with query: {}", search);

        String searchParam = (search == null || search.trim().isEmpty())
                ? "%"
                : "%" + search.trim() + "%";

        return userRepository.searchUsers(searchParam, pageable)
                .map(UserMapper::toDTO);
    }
}
