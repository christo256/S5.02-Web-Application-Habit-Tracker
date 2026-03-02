package com.habittracker.habit_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HabitStatsResponse {

    private long totalHabits;
    private long completedToday;
    private int totalCurrentStreakSum;
    private int bestStreak;
    private double completionRateToday;
    private int habitsNeedingAttention;
}