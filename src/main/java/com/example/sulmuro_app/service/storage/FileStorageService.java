package com.example.sulmuro_app.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file);

    // 새로 추가
    String upload(MultipartFile file, String subDir);
}
