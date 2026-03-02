package com.habittracker.habit_tracker.controller;

import com.habittracker.habit_tracker.dto.response.AdminStatsResponse;
import com.habittracker.habit_tracker.dto.response.ErrorResponse;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.dto.response.UserResponse;
import com.habittracker.habit_tracker.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Administrative endpoints (ADMIN role required)")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get all users",
            description = "Retrieve the complete list of registered users (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "User list retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "Delete user",
            description = "Delete a user from the system (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "User deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all habits",
            description = "Retrieve all habits from all users in the system (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "All habits retrieved successfully",
                    content = @Content(schema = @Schema(implementation = HabitResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/habits")
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        return ResponseEntity.ok(adminService.getAllHabits());
    }

    @Operation(summary = "Get global statistics",
            description = "Retrieve global system statistics such as total users and total habits (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AdminStatsResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}