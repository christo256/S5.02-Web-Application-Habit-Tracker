package com.habittracker.habit_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalHabits;
}