package com.habittracker.habit_tracker.dto.response;

import com.habittracker.habit_tracker.model.enums.Frequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponse {

    private Long id;
    private String name;
    private String description;
    private Frequency frequency;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastCompleted;
    private Long userId;
}