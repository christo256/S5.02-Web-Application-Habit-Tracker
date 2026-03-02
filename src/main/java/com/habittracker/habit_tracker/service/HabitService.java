package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.mapper.HabitMapper;
import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.dto.response.HabitStatsResponse;
import com.habittracker.habit_tracker.exceptions.*;
import com.habittracker.habit_tracker.model.Habit;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.repository.HabitRepository;
import com.habittracker.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service que maneja toda la lógica de hábitos.
 */
@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;

    @Transactional(readOnly = true)
    public List<HabitResponse> getAllUserHabits(String username) {
        User user = findUserByUsername(username);

        return habitRepository.findByUserId(user.getId())
                .stream()
                .map(habitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HabitResponse getHabitById(Long habitId, String username) {
        Habit habit = findHabitById(habitId);
        validateOwnership(habit, username);

        return habitMapper.toResponse(habit);
    }


    @Transactional
    public HabitResponse createHabit(HabitRequest request, String username) {
        User user = findUserByUsername(username);

        Habit habit = habitMapper.toEntity(request, user);
        Habit savedHabit = habitRepository.save(habit);

        return habitMapper.toResponse(savedHabit);
    }


    @Transactional
    public HabitResponse updateHabit(Long habitId, HabitRequest request, String username) {
        Habit habit = findHabitById(habitId);
        validateOwnership(habit, username);

        habitMapper.updateEntity(habit, request);
        Habit updatedHabit = habitRepository.save(habit);

        return habitMapper.toResponse(updatedHabit);
    }


    @Transactional
    public void deleteHabit(Long habitId, String username) {
        Habit habit = findHabitById(habitId);
        validateOwnership(habit, username);

        habitRepository.delete(habit);
    }


    @Transactional
    public HabitResponse completeHabit(Long habitId, String username) {
        Habit habit = findHabitById(habitId);
        validateOwnership(habit, username);

        LocalDate today = LocalDate.now();

        // Validar que no se complete dos veces el mismo día
        if (habit.getLastCompleted() != null && habit.getLastCompleted().equals(today)) {
            throw new BadRequestException("Habit already completed today");
        }

        updateStreak(habit, today);

        habit.setLastCompleted(today);

        Habit savedHabit = habitRepository.save(habit);
        return habitMapper.toResponse(savedHabit);
    }


    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", username
                ));
    }

    private Habit findHabitById(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habit", "id", habitId
                ));
    }


    private void validateOwnership(Habit habit, String username) {
        if (!habit.getUser().getUsername().equals(username)) {
            throw new ForbiddenException(
                    "You don't have permission to access this habit"
            );
        }
    }


    private void updateStreak(Habit habit, LocalDate today) {
        LocalDate lastCompleted = habit.getLastCompleted();

        if (lastCompleted == null) {
            habit.setCurrentStreak(1);
        } else if (lastCompleted.equals(today.minusDays(1))) {
            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        } else {
            // Se rompió la racha → reiniciar
            habit.setCurrentStreak(1);
        }

        // Actualizar récord
        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }
    }

    @Transactional(readOnly = true)
    public HabitStatsResponse getUserStats(String username) {

        List<Habit> habits = habitRepository.findByUserUsername(username);

        long totalHabits = habits.size();

        LocalDate today = LocalDate.now();

        long completedToday = habits.stream()
                .filter(h -> today.equals(h.getLastCompleted()))
                .count();

        int totalCurrentStreakSum = habits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .sum();

        int bestStreak = habits.stream()
                .mapToInt(Habit::getLongestStreak)
                .max()
                .orElse(0);

        double completionRateToday = 0.0;

        if (totalHabits > 0) {
            completionRateToday = (completedToday * 100.0) / totalHabits;
            completionRateToday = Math.round(completionRateToday * 100.0) / 100.0;
        }

        long habitsNeedingAttention = habits.stream()
                .filter(h -> !today.equals(h.getLastCompleted()))
                .count();

        return new HabitStatsResponse(
                totalHabits,
                completedToday,
                totalCurrentStreakSum,
                bestStreak,
                completionRateToday,
                (int) habitsNeedingAttention
        );
    }
}