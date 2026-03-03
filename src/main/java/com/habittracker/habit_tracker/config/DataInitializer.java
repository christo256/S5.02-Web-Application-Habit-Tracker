package com.habittracker.habit_tracker.config;

import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.model.enums.Role;
import com.habittracker.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        var existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isPresent()) {

            User admin = existingAdmin.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            log.info("✅ Admin user updated: username=admin, password=admin123");
        } else {
            // Si no existe, CREAR
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user created: username=admin, password=admin123");
        }
    }
}