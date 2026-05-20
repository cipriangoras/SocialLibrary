package app.SocialLibraryAPI.auth;

import app.SocialLibraryAPI.auth.dto.LoginRequest;
import app.SocialLibraryAPI.auth.dto.RegisterRequest;
import app.SocialLibraryAPI.auth.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
        log.info("REST request to register user with email: {}", registerRequest.getEmail());
        return ResponseEntity.status(201).body(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        log.info("REST request to login user with email: {}", loginRequest.getEmail());
        return ResponseEntity.status(200).body(authService.login(loginRequest));
    }

}