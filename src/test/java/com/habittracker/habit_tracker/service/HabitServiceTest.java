package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.model.Habit;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.model.enums.Frequency;
import com.habittracker.habit_tracker.model.enums.Role;
import com.habittracker.habit_tracker.repository.HabitRepository;
import com.habittracker.habit_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class HabitServiceTest {

    @Autowired
    private HabitService habitService;

    @MockBean
    private HabitRepository habitRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testCreateHabit_Success() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        Habit habit = Habit.builder()
                .id(1L)
                .name("Read")
                .description("Read 30 min")
                .frequency(Frequency.DAILY)
                .user(user)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(habitRepository.save(any(Habit.class))).thenReturn(habit);

        HabitRequest request = new HabitRequest("Read", "Read 30 min", Frequency.DAILY);
        HabitResponse response = habitService.createHabit(request, "testuser");

        assertNotNull(response);
        assertEquals("Read", response.getName());
        assertEquals(Frequency.DAILY, response.getFrequency());
    }
}