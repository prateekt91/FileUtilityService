package com.prat.fileutility.serice;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface FileManagementService {

    List<String> listFiles();

    String uploadFile(MultipartFile file, Instant time);

    ResponseEntity<Resource> downloadFile(String fileName);
}
