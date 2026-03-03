package com.habittracker.habit_tracker.service;

import com.habittracker.habit_tracker.dto.mapper.HabitMapper;
import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.dto.response.HabitStatsResponse;
import com.habittracker.habit_tracker.dto.response.RankingEntry;
import com.habittracker.habit_tracker.exceptions.*;
import com.habittracker.habit_tracker.model.Habit;
import com.habittracker.habit_tracker.model.User;
import com.habittracker.habit_tracker.model.enums.Frequency;
import com.habittracker.habit_tracker.repository.HabitRepository;
import com.habittracker.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service que maneja toda la lógica de hábitos.
 */
@Slf4j
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
        log.info("Creating habit '{}' for user '{}'", request.getName(), username);
        User user = findUserByUsername(username);

        Habit habit = habitMapper.toEntity(request, user);
        Habit savedHabit = habitRepository.save(habit);

        log.info("Habit created successfully with ID: {}", savedHabit.getId());
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
        LocalDate lastCompleted = habit.getLastCompleted();

        // Validar que no se haya completado hoy
        if (today.equals(lastCompleted)) {
            throw new BadRequestException("Habit already completed today");
        }

        // Actualizar según frecuencia
        switch (habit.getFrequency()) {
            case DAILY:
                updateDailyStreak(habit, today, lastCompleted);
                break;

            case WEEKLY:
                updateWeeklyStreak(habit, today, lastCompleted);
                break;

            case MONTHLY:
                updateMonthlyStreak(habit, today, lastCompleted);
                break;
        }

        habit.setLastCompleted(today);
        Habit updatedHabit = habitRepository.save(habit);

        return habitMapper.toResponse(updatedHabit);
    }

    // Lógica DAILY
    private void updateDailyStreak(Habit habit, LocalDate today, LocalDate lastCompleted) {
        if (lastCompleted == null || lastCompleted.isBefore(today.minusDays(1))) {
            // Se rompió la racha
            habit.setCurrentStreak(1);
        } else if (lastCompleted.equals(today.minusDays(1))) {
            // Continúa la racha
            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        }

        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }
    }

    // Lógica WEEKLY
    private void updateWeeklyStreak(Habit habit, LocalDate today, LocalDate lastCompleted) {
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);

        if (lastCompleted == null || lastCompleted.isBefore(startOfLastWeek)) {
            habit.setCurrentStreak(1);
        } else if (!lastCompleted.isBefore(startOfThisWeek)) {

        } else if (!lastCompleted.isBefore(startOfLastWeek)) {

            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        }

        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }
    }

    // Lógica MONTHLY
    private void updateMonthlyStreak(Habit habit, LocalDate today, LocalDate lastCompleted) {
        LocalDate startOfThisMonth = today.withDayOfMonth(1);
        LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);

        if (lastCompleted == null || lastCompleted.isBefore(startOfLastMonth)) {

            habit.setCurrentStreak(1);
        } else if (!lastCompleted.isBefore(startOfThisMonth)) {

        } else if (!lastCompleted.isBefore(startOfLastMonth)) {

            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        }

        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }
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
    // Contar cuántas veces se completó en la semana actual
    private long countCompletionsThisWeek(Long habitId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);


        Habit habit = habitRepository.findById(habitId).orElseThrow();

        if (habit.getLastCompleted() != null &&
                !habit.getLastCompleted().isBefore(startOfWeek)) {
            return 1;
        }
        return 0;
    }

    // Contar cuántas veces se completó en el mes actual
    private long countCompletionsThisMonth(Long habitId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        Habit habit = habitRepository.findById(habitId).orElseThrow();

        if (habit.getLastCompleted() != null &&
                !habit.getLastCompleted().isBefore(startOfMonth)) {
            return 1;
        }
        return 0;
    }

    @Transactional(readOnly = true)
    public List<RankingEntry> getRankingsByFrequency(Frequency frequency) {
        // Obtener top 10 hábitos de esa frecuencia
        Pageable topTen = PageRequest.of(0, 10);
        List<Habit> topHabits = habitRepository.findTopByFrequency(frequency, (topTen));

        // Mapear a RankingEntry
        return topHabits.stream()
                .map(habit -> new RankingEntry(
                        habit.getId(),
                        habit.getName(),
                        habit.getUser().getUsername(),
                        habit.getUser().getId(),
                        habit.getCurrentStreak(),
                        habit.getLongestStreak(),
                        habit.getFrequency().name()
                ))
                .collect(Collectors.toList());
    }


}