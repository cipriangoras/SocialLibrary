//package app.SocialLibraryAPI.core.config;
//
//import app.SocialLibraryAPI.user.Role;
//import app.SocialLibraryAPI.user.UserEntity;
//import app.SocialLibraryAPI.user.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class DataSeederConfig {
//
//    // ruleaza o singura data la pornirea aplicatiei
//    @Bean
//    CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
//        return args -> {
//            String email = "admin@local";
//            if (!repo.existsByEmail(email)) {
//                UserEntity admin = new UserEntity();
//                admin.setFullName("admin");
//                admin.setEmail(email);
//                admin.setAge(0);
//                admin.setPassword(encoder.encode("admin123"));
//                admin.setRole(Role.ADMIN);
//                repo.save(admin);
//            }
//        };
//    }
//}
