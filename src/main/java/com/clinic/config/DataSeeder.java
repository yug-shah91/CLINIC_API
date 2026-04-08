package com.clinic.config;

import com.clinic.entity.User;
import com.clinic.entity.enums.Role;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only create if no admin exists yet
        if (!userRepository.existsByEmail("admin@clinic.com")) {
            User admin = User.builder()
                    .name("Dr. Sharma")
                    .email("admin@clinic.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin created: admin@clinic.com");
        }
    }
}