package com.example.sulmuro_app.controller.image;

import com.example.sulmuro_app.dto.image.place.response.PlaceImageResponse;
import com.example.sulmuro_app.service.image.PlaceImageService;
import com.example.sulmuro_app.dto.bin.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places/{placeId}/images")
public class PlaceImageController {

    private final PlaceImageService placeImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<PlaceImageResponse>> upload(
            @PathVariable Long placeId,
            @RequestPart("files") List<MultipartFile> files
    ) throws IOException {
        var data = placeImageService.upload(placeId, files);
        return ApiResponse.success("이미지가 등록되었습니다.", data);
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(@PathVariable Long placeId) {
        var content = placeImageService.list(placeId);
        return ApiResponse.success("이미지 목록입니다.",
                Map.of("content", content, "total_elements", content.size()));
    }
}
