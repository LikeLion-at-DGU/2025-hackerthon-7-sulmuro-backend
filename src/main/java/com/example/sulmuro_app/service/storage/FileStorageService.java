// src/main/java/com/example/sulmuro_app/service/storage/FileStorageService.java
package com.example.sulmuro_app.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class FileStorageService {

    @Value("${app.upload.root:${user.home}/uploads}")
    private String uploadRoot;

    @Value("${app.upload.public-base-url:http://localhost:8080/uploads}")
    private String publicBaseUrl;

    /** dir: "places/16" 또는 "articles/23" */
    public String upload(MultipartFile file, String dir) {
        try {
            String safeDir = dir.replace("..", "").replace("\\", "/");
            String ext = extOf(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;

            Path base = Paths.get(uploadRoot, safeDir).normalize();
            Files.createDirectories(base);

            Path dest = base.resolve(filename).normalize();
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, REPLACE_EXISTING);
            }

            String rel = (safeDir + "/" + filename).replace("\\", "/");     // 상대경로
            String baseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
            return baseUrl + rel;                                           // 절대 URL 반환
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private String extOf(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i > -1 && i < name.length() - 1) ? name.substring(i) : "";
    }
}
