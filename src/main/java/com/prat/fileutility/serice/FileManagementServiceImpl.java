package com.prat.fileutility.serice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileManagementServiceImpl implements FileManagementService {

    @Value("${file.directory.path}")
    private String directoryPath;

    private Logger log = LoggerFactory.getLogger(FileManagementServiceImpl.class);

    @Override
    public List<String> listFiles() {

        log.info("Inside FileManagementServiceImpl.listFiles() method");

        // Logic to list files in a directory would go here
        Path fileDirectoryPath = Paths.get(directoryPath);

        try {
            if (!Files.exists(fileDirectoryPath)) {
                Files.createDirectories(fileDirectoryPath);
            }

            try(Stream<Path> paths = Files.list(fileDirectoryPath)) {
                List<String> fileList = paths
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .toList();

                return fileList.isEmpty() ? List.of("No Files Present") : fileList;
            }
        } catch (Exception e) {
           log.error("Error listing files in directory: {}", directoryPath, e);
           return List.of("No Files Present");
        }
    }

    @Override
    public String uploadFile(MultipartFile file, Instant time) {

        log.info("Inside FileManagementServiceImpl.uploadFile() method");
        Assert.notNull(file, "File cannot be null");

        String fileName = file.getOriginalFilename();
        try {
            Path filePath = Paths.get(directoryPath, fileName);
            Files.write(filePath, file.getBytes());
            Instant currentTime = Instant.now();
            long timeTook = (currentTime.toEpochMilli() - time.toEpochMilli())/1000;
            log.info("Time taken to upload file: {} is: {} s", fileName, timeTook);
            return "File uploaded successfully: " + fileName + " It took " +timeTook+ " seconds";
        } catch (Exception e) {
            log.error("Error uploading file: {}", fileName, e);
            return "Error uploading file: " + fileName;
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileName) {

        log.info("Inside FileManagementServiceImpl.downloadFile() method");
        try {

            Assert.notNull(fileName, "File name cannot be null");

            Path filePath = Paths.get(directoryPath, fileName);
            Resource resource = new FileSystemResource(filePath.toFile());

            if (!resource.exists()) {
                log.error("File not found: {}", fileName);
                throw new RuntimeException("File not found: " + fileName);
            }

            log.info("File found and accessible: {}", filePath.getFileName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading file: {}", fileName, e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
