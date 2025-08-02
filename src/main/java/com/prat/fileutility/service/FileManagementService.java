package com.prat.fileutility.service;

import com.prat.fileutility.model.FileDesc;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface FileManagementService {

    List<FileDesc> listFiles();

    String uploadFiles(List<MultipartFile> files, Instant time);

    ResponseEntity<Resource> downloadFile(String fileName);

    ResponseEntity<Resource> downloadResizedFile(String fileName, Integer width, Integer height, String quality);

    ResponseEntity<String> deleteFiles(List<String> fileNames);

}
