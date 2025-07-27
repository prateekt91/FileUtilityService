package com.prat.graalvmdemo.serice;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileManagementService {

    List<String> listFiles();

    String uploadFile();

    ResponseEntity<Resource> downloadFile(String fileName);
}
