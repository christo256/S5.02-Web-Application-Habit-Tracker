package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.request.LoginRequest;
import com.habittracker.habit_tracker.dto.request.RegisterRequest;
import com.habittracker.habit_tracker.dto.response.JwtResponse;
import com.habittracker.habit_tracker.exceptions.*;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.model.enums.Role;
import com.habittracker.habit_tracker.repository.UserRepository;
import com.habittracker.habit_tracker.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("newuser", "password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertDoesNotThrow(() -> authService.register(request));
    }

    @Test
    void testRegister_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existinguser", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt-token-123");

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}