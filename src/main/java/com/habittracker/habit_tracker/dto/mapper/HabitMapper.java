package com.habittracker.habit_tracker.dto.mapper;


import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.model.Habit;
import com.habittracker.habit_tracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {


    public HabitResponse toResponse(Habit habit) {
        return HabitResponse.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .frequency(habit.getFrequency())
                .currentStreak(habit.getCurrentStreak())
                .longestStreak(habit.getLongestStreak())
                .lastCompleted(habit.getLastCompleted())
                .userId(habit.getUser().getId())
                .build();
    }


    public Habit toEntity(HabitRequest request, User user) {
        return Habit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .frequency(request.getFrequency())
                .currentStreak(0)
                .longestStreak(0)
                .user(user)
                .build();
    }


    public void updateEntity(Habit habit, HabitRequest request) {
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setFrequency(request.getFrequency());

    }
}