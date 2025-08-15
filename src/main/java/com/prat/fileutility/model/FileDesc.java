package com.prat.fileutility.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "fileDesc")
@Schema(description = "File metadata information")
public record FileDesc (
        @MongoId
        @Schema(description = "Unique identifier for the file", example = "60c72b2f5e7e2a1234567890")
        String _id,
        
        @Schema(description = "Name of the file", example = "document.pdf")
        String name,
        
        @Schema(description = "MIME type of the file", example = "application/pdf")
        String type,
        
        @Schema(description = "Size of the file in bytes", example = "1048576")
        long size,
        
        @Schema(description = "Last modification timestamp", example = "2023-10-15T14:30:00")
        LocalDateTime modified,
        
        @Schema(description = "Storage location path", example = "src/main/resources/FileDirectory/document.pdf")
        String location){
}
