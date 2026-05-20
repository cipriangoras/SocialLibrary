package app.SocialLibraryAPI.auth;

import app.SocialLibraryAPI.auth.dto.LoginRequest;
import app.SocialLibraryAPI.auth.dto.RegisterRequest;
import app.SocialLibraryAPI.auth.dto.AuthResponse;
import app.SocialLibraryAPI.user.Role;
import app.SocialLibraryAPI.user.UserEntity;
import app.SocialLibraryAPI.user.UserRepository;
import app.SocialLibraryAPI.core.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest registerRequest) {

        if( !registerRequest.getPassword().equals(registerRequest.getCheckPassword())){
            throw new IllegalArgumentException("Passwords don't match");
        }

        var userEntity = new UserEntity();
        userEntity.setFullName(registerRequest.getFullName());
        userEntity.setEmail(registerRequest.getEmail());
        userEntity.setAge(0);
        userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userEntity.setRole(Role.USER);

        userRepository.save(userEntity);
        var jwtToken = jwtService.generateToken(userEntity);

        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        var userEntity = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User doesn't exists. Email: " + loginRequest.getEmail())
        );

        var jwtToken = jwtService.generateToken(userEntity);

        return new AuthResponse(jwtToken);

    }



}
