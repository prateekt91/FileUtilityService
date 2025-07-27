package com.prat.graalvmdemo.serice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public String uploadFile() {
        return "";
    }

    @Override
    public File downloadFile() {
        return null;
    }
}
