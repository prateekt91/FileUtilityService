package com.prat.fileutility.controller;

import com.prat.fileutility.exception.InvalidFileException;
import com.prat.fileutility.model.FileDesc;
import com.prat.fileutility.service.FileManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Operations", description = "API endpoints for file management operations including upload, download, resize, and delete")
public class FileController {


    private final FileManagementService fileManagementService;

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    public FileController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @Operation(summary = "List all files", description = "Retrieves a list of all files stored in the system with their metadata")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of files", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = FileDesc.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving files")
    })
    @GetMapping("/list")
    public ResponseEntity<List<FileDesc>> listFiles() {
        log.info("Inside FileController.listFiles() method");
        log.info("Listing files in the directory");

        return ResponseEntity.ok(fileManagementService.listFiles());
    }

    @Operation(summary = "Upload files", description = "Uploads one or more files to the server")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Files successfully uploaded", 
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid request - no files provided or files are invalid"),
        @ApiResponse(responseCode = "413", description = "Files exceed the maximum allowed size (20GB)"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred during file upload")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "List of files to upload. Each file must not exceed 20GB.", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        Instant time = Instant.now();
        log.info("Inside FileController.uploadFile() method");
        
        // Validate files parameter
        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("No files were provided for upload");
        }
        
        return ResponseEntity.ok(fileManagementService.uploadFiles(files, time));
    }

    @Operation(summary = "Download a file", description = "Downloads a file by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File successfully downloaded", 
                    content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "400", description = "Invalid request - file name is empty"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred during file download")
    })
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Name of the file to download", required = true, example = "example.jpg")
            @RequestParam String fileName) {
        // Validate fileName parameter
        if (!StringUtils.hasText(fileName)) {
            throw new InvalidFileException("File name cannot be empty");
        }
        
        log.info("Inside FileController.downloadFile() method for file: {}", fileName);

        return fileManagementService.downloadFile(fileName);
    }

    @Operation(summary = "Download a resized image file", 
              description = "Downloads an image file with optional resizing parameters. Only works with image files.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image file successfully downloaded with specified dimensions", 
                    content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "400", description = "Invalid request - file name is empty, dimensions are invalid, or quality value is invalid"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "415", description = "Unsupported media type - file is not an image"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred during file processing")
    })
    @GetMapping("/download/resize")
    public ResponseEntity<Resource> downloadResizedFile(
            @Parameter(description = "Name of the image file to download and resize", required = true, example = "image.jpg")
            @RequestParam String fileName,
            @Parameter(description = "Desired width of the image in pixels. Must be a positive number if provided.", example = "800")
            @RequestParam(required = false) Integer width,
            @Parameter(description = "Desired height of the image in pixels. Must be a positive number if provided.", example = "600")
            @RequestParam(required = false) Integer height,
            @Parameter(description = "Resizing quality. Affects the algorithm used for resizing.", 
                      schema = @Schema(type = "string", allowableValues = {"AUTOMATIC", "SPEED", "BALANCED", "QUALITY", "SMOOTH"}, defaultValue = "SMOOTH"))
            @RequestParam(defaultValue = "SMOOTH") String quality) {

        // Validate fileName parameter
        if (!StringUtils.hasText(fileName)) {
            throw new InvalidFileException("File name cannot be empty");
        }
        
        // Validate width and height parameters if provided
        if (width != null && width <= 0) {
            throw new InvalidFileException("Width must be a positive number");
        }
        
        if (height != null && height <= 0) {
            throw new InvalidFileException("Height must be a positive number");
        }
        
        // Validate quality parameter
        if (!quality.equals("AUTOMATIC") && !quality.equals("SPEED") && 
            !quality.equals("BALANCED") && !quality.equals("ULTRA_QUALITY") && !quality.equals("SMOOTH")) {
            throw new InvalidFileException("Invalid quality parameter. Allowed values: AUTOMATIC, SPEED, BALANCED, QUALITY, SMOOTH");
        }

        log.info("Downloading a resized file: {} with dimensions: {}x{}", fileName, width, height);
        return fileManagementService.downloadResizedFile(fileName, width, height, quality);
    }

    @Operation(summary = "Delete files", description = "Deletes one or more files by their names")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Files successfully deleted", 
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid request - no file names provided or file names are empty"),
        @ApiResponse(responseCode = "404", description = "One or more files not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred during file deletion")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFiles(
            @Parameter(description = "List of file names to delete", required = true, example = "[\"file1.txt\", \"file2.jpg\"]")
            @RequestParam("fileNames") List<String> fileNames) {
        log.info("Inside method FileController.deleteFiles()");
        
        // Validate fileNames parameter
        if (fileNames == null || fileNames.isEmpty()) {
            throw new InvalidFileException("No file names were provided for deletion");
        }
        
        // Validate each file name in the list
        for (String fileName : fileNames) {
            if (!StringUtils.hasText(fileName)) {
                throw new InvalidFileException("File name cannot be empty");
            }
        }
        
        return fileManagementService.deleteFiles(fileNames);
    }

}
