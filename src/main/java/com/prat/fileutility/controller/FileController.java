package com.prat.fileutility.controller;

import com.prat.fileutility.model.FileDesc;
import com.prat.fileutility.service.FileManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {


    private final FileManagementService fileManagementService;

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    public FileController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDesc>> listFiles() {

        log.info("Inside FileController.listFiles() method");
        log.info("Listing files in the directory");

        return ResponseEntity.ok(fileManagementService.listFiles());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        Instant time = Instant.now();
        log.info("Inside FileController.uploadFile() method");
        log.info("Uploading file: {} with size: {} bytes", file.getOriginalFilename(), file.getSize());
        // Logic to upload a file would go here
        return ResponseEntity.ok(fileManagementService.uploadFile(file, time));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
                                                // @RequestHeader(value = "Range", required = false) String rangeHeader) {
        // Logic to download a file would go here
        log.info("Inside FileController.downloadFile() method for file: {}", fileName);

//        if (rangeHeader != null) {
//            log.info("Range request detected: {}", rangeHeader);
//            return fileManagementService.streamMediaFile(fileName, rangeHeader);
//        }

        return fileManagementService.downloadFile(fileName);
    }

    @GetMapping("/download/resize")
    public ResponseEntity<Resource> downloadResizedFile(
            @RequestParam String fileName,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(defaultValue = "SMOOTH") String quality) {

        log.info("Downloading resized file: {} with dimensions: {}x{}", fileName, width, height);
        return fileManagementService.downloadResizedFile(fileName, width, height, quality);
    }

}
