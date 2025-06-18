package com.skillverify.authservice.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.skillverify.authservice.entity.User;
import com.skillverify.authservice.repository.UserRepository;
import com.skillverify.authservice.utils.Role;

import jakarta.annotation.PostConstruct;

@Component
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("admin@skillverify.com")) {
            User admin = User.builder()
                    .name("Default Admin")
                    .email("admin@skillverify.com")
                    .password(passwordEncoder.encode("admin123")) // Choose a strong default
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Default admin created.");
        } else {
            System.out.println("✅ Admin already exists. Skipping creation.");
        }
    }
}