package app.SocialLibraryAPI.controller;


import app.SocialLibraryAPI.dto.request.LoginRequest;
import app.SocialLibraryAPI.dto.request.RegisterRequest;
import app.SocialLibraryAPI.dto.response.AuthResponse;
import app.SocialLibraryAPI.dto.response.UserDTO;
import app.SocialLibraryAPI.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // to do the logging
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(201).body(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.status(200).body(authService.login(loginRequest));

    }


}
