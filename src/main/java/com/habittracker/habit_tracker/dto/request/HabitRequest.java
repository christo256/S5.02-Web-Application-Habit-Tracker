package com.habittracker.habit_tracker.dto.request;

import com.habittracker.habit_tracker.model.enums.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
}
