package com.habittracker.habit_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingEntry {
    private Long habitId;
    private String habitName;
    private String username;
    private Long userId;
    private Integer currentStreak;
    private Integer longestStreak;
    private String frequency;
}