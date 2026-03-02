package com.habittracker.habit_tracker.controller;

import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.ErrorResponse;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.dto.response.HabitStatsResponse;
import com.habittracker.habit_tracker.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Habits", description = "Habit management endpoints (authentication required)")
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
public class HabitController {

    private final HabitService habitService;

    @Operation(summary = "Get all habits",
            description = "Retrieve all habits belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "List of habits retrieved successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits(Authentication authentication) {
        return ResponseEntity.ok(
                habitService.getAllUserHabits(authentication.getName())
        );
    }

    @Operation(summary = "Get habit by ID",
            description = "Retrieve a specific habit belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Habit found",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "You do not have permission to access this habit",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getHabitById(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(
                habitService.getHabitById(id, authentication.getName())
        );
    }

    @Operation(summary = "Create new habit",
            description = "Create a new habit for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Habit created successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid habit data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(@Valid @RequestBody HabitRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.createHabit(request, authentication.getName()));
    }

    @Operation(summary = "Update habit",
            description = "Update an existing habit (name, description and frequency only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Habit updated successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "You do not have permission to update this habit",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(@PathVariable Long id,
                                                     @Valid @RequestBody HabitRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(
                habitService.updateHabit(id, request, authentication.getName())
        );
    }

    @Operation(summary = "Delete habit",
            description = "Delete a habit belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Habit deleted successfully"),
            @ApiResponse(responseCode = "403",
                    description = "You do not have permission to delete this habit",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id,
                                            Authentication authentication) {
        habitService.deleteHabit(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Complete habit",
            description = "Mark the habit as completed today. Streaks are calculated and updated automatically.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Habit marked as completed, streaks updated",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Habit already completed today",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Habit not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<HabitResponse> completeHabit(@PathVariable Long id,
                                                       Authentication authentication) {
        return ResponseEntity.ok(
                habitService.completeHabit(id, authentication.getName())
        );
    }

    @Operation(summary = "Get personal habit statistics",
            description = "Retrieve statistics related to the authenticated user's habits")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = HabitStatsResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - invalid or missing token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<HabitStatsResponse> getUserStats(Authentication authentication) {
        return ResponseEntity.ok(
                habitService.getUserStats(authentication.getName())
        );
    }
}