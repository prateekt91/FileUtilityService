package com.prat.fileutility.exception;

import com.prat.fileutility.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(
            FileNotFoundException ex, HttpServletRequest request) {
        log.error("File not found exception: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(
            FileStorageException ex, HttpServletRequest request) {
        log.error("File storage exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(
            InvalidFileException ex, HttpServletRequest request) {
        log.error("Invalid file exception: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseOperationException(
            DatabaseOperationException ex, HttpServletRequest request) {
        log.error("Database operation exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("Max upload size exceeded: {}", ex.getMessage());
        
        // Create a custom error message that's more user-friendly
        String errorMessage = "The uploaded file exceeds the maximum allowed size. Maximum allowed size is 20GB.";
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        if (shouldIncludeStackTrace()) {
            errorResponse.setTrace(getStackTraceAsString(ex));
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("Missing request parameter: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, HttpStatus status, HttpServletRequest request) {
        
        // Create a consistent error response
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "An error occurred")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        // Only include stack trace in non-production environments
        if (shouldIncludeStackTrace()) {
            errorResponse.setTrace(getStackTraceAsString(ex));
        }
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    private boolean shouldIncludeStackTrace() {
        // Only include stack trace in non-production environments
        String activeProfile = System.getProperty("spring.profiles.active", "dev");
        return !activeProfile.equalsIgnoreCase("prod") && !activeProfile.equalsIgnoreCase("production");
    }
    
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}