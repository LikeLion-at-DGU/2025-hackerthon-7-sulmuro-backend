package com.example.sulmuro_app.service.storage;

import org.springframework.beans.factory.annotation.Value;   // ✅ 스프링 @Value 로 변경!
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    /** 실제 저장 루트 (예: 프로젝트 루트/uploads) */
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /** 외부에서 접근할 URL prefix (정적 리소스 매핑과 일치) */
    @Value("${app.upload.base-url:/uploads}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file) {
        return upload(file, null);
    }

    @Override
    public String upload(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        try {
            // 최종 저장 경로: {uploadDir}/{subDir}/{uuid}.{ext}
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")) : "";
            String savedName = UUID.randomUUID() + ext;

            String normalizedSub = (subDir == null || subDir.isBlank())
                    ? ""
                    : subDir.replaceAll("^/+", "").replaceAll("/+$", "");

            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path targetDir = normalizedSub.isEmpty() ? base : base.resolve(normalizedSub);
            Files.createDirectories(targetDir);

            Path target = targetDir.resolve(savedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // 반환 URL: {baseUrl}/{subDir}/{filename}
            String prefix = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            String url = normalizedSub.isEmpty()
                    ? prefix + "/" + savedName
                    : prefix + "/" + normalizedSub.replace("\\", "/") + "/" + savedName;

            return url;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}
