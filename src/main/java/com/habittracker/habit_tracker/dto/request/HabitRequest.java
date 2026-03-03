package com.habittracker.habit_tracker.dto.request;

import com.habittracker.habit_tracker.model.enums.Frequency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitRequest {
    @NotBlank(message = "Habit name is required")
    @Size(max = 100, message = "Habit name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Frequency is required")
    private Frequency frequency;

    @NotNull(message = "Target count is required")
    @Min(value = 1, message = "Target count must be at least 1")
    @Max(value = 31, message = "Target count cannot exceed 31")
    private Integer targetCount;
}
