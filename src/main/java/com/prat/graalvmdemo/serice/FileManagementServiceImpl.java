package com.prat.graalvmdemo.serice;

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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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
    public String uploadFile() {
        return "";
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
