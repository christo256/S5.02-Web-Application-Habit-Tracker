package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.mapper.HabitMapper;
import com.habittracker.habit_tracker.dto.mapper.UserMapper;
import com.habittracker.habit_tracker.dto.response.AdminStatsResponse;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.dto.response.UserResponse;
import com.habittracker.habit_tracker.exceptions.ResourceNotFoundException;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.repository.HabitRepository;
import com.habittracker.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final UserMapper userMapper;
    private final HabitMapper habitMapper;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.delete(user);
    }

    public List<HabitResponse> getAllHabits() {
        return habitRepository.findAll()
                .stream()
                .map(habitMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdminStatsResponse getStats() {

        long totalUsers = userRepository.count();
        long totalHabits = habitRepository.count();

        return new AdminStatsResponse(totalUsers, totalHabits);
    }
}