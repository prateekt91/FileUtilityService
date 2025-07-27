package com.prat.graalvmdemo.serice;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

public interface FileManagementService {

    List<String> listFiles();

    String uploadFile();

    File downloadFile();
}
