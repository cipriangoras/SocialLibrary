package app.SocialLibraryAPI.service;

import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.mappers.UserMapper;
import app.SocialLibraryAPI.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FollowService {

    private final UserRepository userRepository;

    public FollowService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void followUser(String currentUserEmail, Long targetUserId) {
        log.info("Attempting to make user {} follow user id: {}", currentUserEmail, targetUserId);

        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> {
                    log.error("Follow failed. Current user not found with email: {}", currentUserEmail);
                    return new EntityNotFoundException("Current user not found with email: " + currentUserEmail);
                });

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    log.error("Follow failed. Target user not found with id: {}", targetUserId);
                    return new EntityNotFoundException("Target user not found with id: " + targetUserId);
                });

        if (currentUser.getId().equals(targetUser.getId())) {
            log.error("Follow failed. User {} attempted to follow themselves", currentUser.getId());
            throw new IllegalStateException("You cannot follow yourself!");
        }

        currentUser.follow(targetUser);
        userRepository.save(currentUser);
        log.info("Successfully updated follow. User {} is now following User {}", currentUser.getId(), targetUser.getId());
    }

    @Transactional
    public void unfollowUser(String currentUserEmail, Long targetUserId) {
        log.info("Attempting to make user {} unfollow user id: {}", currentUserEmail, targetUserId);

        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> {
                    log.error("Unfollow failed. Current user not found with email: {}", currentUserEmail);
                    return new EntityNotFoundException("Current user not found with email: " + currentUserEmail);
                });

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    log.error("Unfollow failed. Target user not found with id: {}", targetUserId);
                    return new EntityNotFoundException("Target user not found with id: " + targetUserId);
                });

        currentUser.unfollow(targetUser);
        userRepository.save(currentUser);
        log.info("Successfully updated follow. User {} unfollowed User {}", currentUser.getId(), targetUser.getId());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getFollowers(Long userId) {
        log.info("Fetching followers for user id: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Failed to fetch followers. User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        List<UserDTO> followers = user.getFollowers().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} followers for user id: {}", followers.size(), userId);
        return followers;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getFollowing(Long userId) {
        log.info("Fetching following list for user id: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Failed to fetch following. User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        List<UserDTO> following = user.getFollowing().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} following for user id: {}", following.size(), userId);
        return following;
    }
}