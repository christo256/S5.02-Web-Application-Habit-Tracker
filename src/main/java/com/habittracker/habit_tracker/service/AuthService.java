package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.request.LoginRequest;
import com.habittracker.habit_tracker.dto.request.RegisterRequest;
import com.habittracker.habit_tracker.dto.response.JwtResponse;
import com.habittracker.habit_tracker.exceptions.*;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.model.enums.Role;
import com.habittracker.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service que maneja la autenticación (login y registro).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists: " + request.getUsername()
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }


        String tempToken = "TEMPORARY_TOKEN_FASE_6";

        return new JwtResponse(
                tempToken,
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}