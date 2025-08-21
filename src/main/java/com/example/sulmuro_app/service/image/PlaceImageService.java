package com.example.sulmuro_app.service.image;

import com.example.sulmuro_app.domain.image.PlaceImage;
import com.example.sulmuro_app.domain.image.PlaceImageRepository;
import com.example.sulmuro_app.domain.place.Place;
import com.example.sulmuro_app.domain.place.PlaceRepository;
import com.example.sulmuro_app.dto.image.place.response.PlaceImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceImageService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;

    @Value("${app.upload.root:${user.home}/uploads}")
    private String uploadRoot;

    @Value("${app.upload.public-base-url:http://localhost:8080/uploads}")
    private String publicBaseUrl;

    /** 특정 장소 이미지 여러 장 업로드 */
    public List<PlaceImageResponse> upload(Long placeId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("files are required");
        }

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("place not found"));

        List<PlaceImage> saved = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;
            // 간단한 이미지 MIME 체크(선택)
            if (f.getContentType() == null || !f.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("only image files are allowed");
            }

            String url = saveToLocal(placeId, f);
            String filename = Optional.ofNullable(f.getOriginalFilename()).filter(s -> !s.isBlank()).orElse("image");

            PlaceImage img = new PlaceImage();
            img.setPlace(place);
            img.setUrl(url);
            img.setFilename(filename);

            saved.add(placeImageRepository.save(img));
        }

        // 커버가 아직 없으면 첫 저장 이미지를 커버로
        if (place.getImage_id() == null && !saved.isEmpty()) {
            place.setImage_id(saved.get(0).getImageId());
        }

        Long coverId = place.getImage_id();
        return saved.stream().map(i -> PlaceImageResponse.of(i, coverId)).collect(Collectors.toList());
    }

    /** 특정 장소 이미지 목록 */
    @Transactional(readOnly = true)
    public List<PlaceImageResponse> list(Long placeId) {
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("장소를 찾을 수 없습니다."));
        Long coverId = place.getImage_id();
        return placeImageRepository.findByPlace_PlaceIdOrderByCreatedAtDesc(placeId)
                .stream()
                .map(i -> PlaceImageResponse.of(i, coverId))
                .collect(Collectors.toList());
    }

    // --- helpers ---
    private String saveToLocal(Long placeId, MultipartFile file) throws IOException {
        String ext = "";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            ext = name.substring(name.lastIndexOf("."));
        }
        String key = "places/" + placeId + "/" + UUID.randomUUID() + ext;
        Path dest = Paths.get(uploadRoot, key);
        Files.createDirectories(dest.getParent());
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return publicBaseUrl + "/" + key;
    }
}
