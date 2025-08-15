package com.prat.fileutility.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard error response format")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type", example = "Bad Request")
    private String error;
    
    @Schema(description = "Detailed error message", example = "File name cannot be empty")
    private String message;
    
    @Schema(description = "Request path that generated the error", example = "/api/files/download")
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp when the error occurred", example = "2023-10-15 14:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Stack trace (only included in non-production environments)", example = "java.lang.Exception: File not found\n\tat com.example...")
    private String trace;
}