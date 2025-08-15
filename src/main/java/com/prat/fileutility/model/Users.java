package com.prat.fileutility.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "users")
@Schema(description = "User information")
public record Users(
        @MongoId
        @Schema(description = "Unique identifier for the user", example = "60c72b2f5e7e2a1234567890")
        String _id,
        
        @Schema(description = "User's first name", example = "Prateek")
        String fname,
        
        @Schema(description = "User's last name", example = "Tiwari")
        String lname,
        
        @Schema(description = "User's email address", example = "prateek.tiwari@example.com")
        String email,
        
        @Schema(description = "User's username for login", example = "prateek91")
        String userName,
        
        @Schema(description = "User's phone number", example = "+91-8095809909")
        String phone,
        
        @Schema(description = "User's location or address", example = "Bengaluru, India")
        String location,
        
        @Schema(description = "User's role in the system", example = "admin")
        String role,
        
        @Schema(description = "User's department", example = "Engineering")
        String department,
        
        @Schema(description = "User's join date", example = "2023-01-15")
        String joindate,
        
        @Schema(description = "User's account status (active/inactive)", example = "true")
        Boolean status,
        
        @Schema(description = "URL to user's avatar image", example = "https://example.com/avatars/prateek91.jpg")
        String avatar) {}
