package app.SocialLibraryAPI.user;

import app.SocialLibraryAPI.user.dto.UpdateProfileRequest;
import app.SocialLibraryAPI.user.dto.UserDTO;
import app.SocialLibraryAPI.user.dto.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/management/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User userToCreate){
        log.info("REST request to create user: {}", userToCreate.email());
        return ResponseEntity.status(201).body(userService.createUser(userToCreate));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        log.info("REST request to fetch all users");
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id){
        log.info("REST request to fetch user with id: {}", id);
        return ResponseEntity.status(200).body(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id){
        log.info("REST request to delete user with id: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.status(200).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable("id") Long id, @RequestBody @Valid User updatedUser){
        log.info("REST request to update user with id: {}", id);
        return ResponseEntity.status(200).body(userService.updateUserById(id, updatedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        UserDTO user = userService.findUserByEmail(principal.getName());
        return ResponseEntity.status(200).body(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            Principal principal) {

        log.info("REST request to update profile for current user: {}", principal.getName());
        return ResponseEntity.status(200).body(userService.updateMyProfile(principal.getName(), request));
    }

}
