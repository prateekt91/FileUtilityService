package com.prat.fileutility.service;

import com.prat.fileutility.model.FileDesc;
import com.prat.fileutility.repository.FileOperationRepository;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileManagementServiceImpl implements FileManagementService {

    @Value("${file.directory.path}")
    private String directoryPath;

    private FileOperationRepository fileOperationRepository;

    private Logger log = LoggerFactory.getLogger(FileManagementServiceImpl.class);


    public FileManagementServiceImpl(FileOperationRepository fileOperationRepository) {
        this.fileOperationRepository = fileOperationRepository;
    }

    @Override
    public List<FileDesc> listFiles() {

        log.info("Inside FileManagementServiceImpl.listFiles() method");

        // Logic to list files in a directory would go here
        Path fileDirectoryPath = Paths.get(directoryPath);
        List<FileDesc> fileDescList = new ArrayList<>();

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

                if(!fileList.isEmpty()) {
                    fileList.forEach(file -> {
                        fileDescList.add(fileOperationRepository.findByName(file));
                    });
                }
                return fileDescList;
            }
        } catch (Exception e) {
           log.error("Error listing files in directory: {}", directoryPath, e);
           return fileDescList;
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

            FileDesc fileDesc = new FileDesc(null, file.getOriginalFilename(), file.getContentType(), file.getSize(), LocalDateTime.now(), directoryPath);
            fileOperationRepository.save(fileDesc);

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

            // Get file information from repository
            FileDesc fileDesc = fileOperationRepository.findByName(fileName);
            String contentType = determineContentType(fileName, fileDesc);

            log.info("File found and accessible: {} with content type: {}", filePath.getFileName(), contentType);

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, buildContentDisposition(fileName, contentType))
                    .contentType(MediaType.parseMediaType(contentType));


            // Add PDF-specific headers for better browser handling
            if (isPdfFile(fileName, contentType)) {
                responseBuilder.header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0");
                log.info("Added PDF-specific headers for file: {}", fileName);
            }

            return responseBuilder.body(resource);

        } catch (Exception e) {
            log.error("Error downloading file: {}", fileName, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    public ResponseEntity<Resource> downloadResizedFile(String fileName, Integer width, Integer height, String quality) {
        log.info("Inside FileManagementServiceImpl.downloadResizedFile() method");

        try {
            Assert.notNull(fileName, "File name cannot be null");

            Path filePath = Paths.get(directoryPath, fileName);
            if (!Files.exists(filePath)) {
                log.error("File not found: {}", fileName);
                throw new RuntimeException("File not found: " + fileName);
            }

            FileDesc fileDesc = fileOperationRepository.findByName(fileName);
            if (fileDesc == null || !isImageFile(fileDesc.type())) {
                // If not an image, return an original file
                return downloadFile(fileName);
            }

            // Resize image
            BufferedImage originalImage = ImageIO.read(filePath.toFile());
            BufferedImage resizedImage = resizeImage(originalImage, width, height, quality);

            // Convert to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = getImageFormat(fileName);
            ImageIO.write(resizedImage, format, baos);
            byte[] imageBytes = baos.toByteArray();

            // Create a resource from a byte array
            Resource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment; filename=\"%s_%dx%d.%s\"",
                                    getFileNameWithoutExtension(fileName),
                                    width != null ? width : originalImage.getWidth(),
                                    height != null ? height : originalImage.getHeight(),
                                    format))
                    .contentType(MediaType.parseMediaType(fileDesc.type()))
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading resized file: {}", fileName, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    private BufferedImage resizeImage(BufferedImage original, Integer width, Integer height, String quality) {
        if (width == null && height == null) {
            return original;
        }

        int targetWidth = width != null ? width : original.getWidth();
        int targetHeight = height != null ? height : original.getHeight();

        // Maintain an aspect ratio if only one dimension is provided
        if (width == null) {
            targetWidth = (int) ((double) targetHeight / original.getHeight() * original.getWidth());
        } else if (height == null) {
            targetHeight = (int) ((double) targetWidth / original.getWidth() * original.getHeight());
        }

        return Scalr.resize(original,
                Scalr.Method.valueOf(quality.toUpperCase()),
                targetWidth, targetHeight);
    }

    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private String getImageFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "jpg";
            case "png" -> "png";
            case "gif" -> "gif";
            default -> "jpg";
        };
    }


    private String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private boolean isPdfFile(String fileName, String contentType) {
        return (contentType != null && contentType.equals("application/pdf")) ||
                fileName.toLowerCase().endsWith(".pdf");
    }

    private String determineContentType(String fileName, FileDesc fileDesc) {
        // First try to get content type from stored file description
        if (fileDesc != null && fileDesc.type() != null) {
            return fileDesc.type();
        }

        // Fallback to determining by file extension
        String extension = getFileExtension(fileName).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }


    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    private String buildContentDisposition(String fileName, String contentType) {
        // For PDFs, use inline disposition to allow browser viewing
        if (isPdfFile(fileName, contentType)) {
            return String.format("inline; filename=\"%s\"", fileName);
        }
        // For other files, use attachment to force download
        return String.format("attachment; filename=\"%s\"", fileName);
    }



}