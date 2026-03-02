package com.habittracker.habit_tracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response returned when a request fails")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error type description")
    private String error;

    @Schema(description = "Detailed error message")
    private String message;

    @Schema(description = "Request path that caused the error")
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}