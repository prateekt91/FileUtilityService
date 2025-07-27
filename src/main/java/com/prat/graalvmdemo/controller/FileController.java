package com.prat.graalvmdemo.controller;

import com.prat.graalvmdemo.serice.FileManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileManagementService fileManagementService;

    private static Logger log = LoggerFactory.getLogger(FileController.class);

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {

        log.info("Inside FileController.listFiles() method");
        log.info("Listing files in the directory");

        return ResponseEntity.ok(fileManagementService.listFiles());
    }

    @PostMapping("/upload")
    public String uploadFile() {
        // Logic to upload a file would go here
        return "File uploaded successfully";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
        // Logic to download a file would go here
        log.info("Inside FileController.downloadFile() method for file: {}", fileName);
        return fileManagementService.downloadFile(fileName);
    }
}
