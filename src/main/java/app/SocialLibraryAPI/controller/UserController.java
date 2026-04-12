package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.User;
import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.entity.UserEntity;
import app.SocialLibraryAPI.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/management/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    private UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User userToCreate){
        log.info("Method createUses was called");
        return ResponseEntity.status(201).body(userService.createUser(userToCreate));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        log.info("Method getAllUsers was called");
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id){
        log.info("Method getUserById was called");
        return ResponseEntity.status(200).body(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id){
        log.info("Method deleteUserById was called");
        userService.deleteUserById(id);
        return ResponseEntity.status(200).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable("id") Long id, @RequestBody @Valid User updatedUser){
        log.info("Method updateUserById was called");
        return ResponseEntity.status(201).body(userService.updateUserById(id, updatedUser));
    }



}
