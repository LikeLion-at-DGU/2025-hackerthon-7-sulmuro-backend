package com.example.sulmuro_app.controller.test;

import com.example.sulmuro_app.dto.test.request.TestCreateRequest;
import com.example.sulmuro_app.dto.test.response.TestResponse;
import com.example.sulmuro_app.service.test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor // Lombok 어노테이션
@RestController
public class TestController {
    private final TestService testService;


    @PostMapping("/test")
    public TestResponse saveTestData(@RequestBody TestCreateRequest request) {
        return testService.saveTestData(request);
    }

    @GetMapping("/")
    public String home() {
        return "술무로 서버가 CI/CD를 포함하여 정상적으로 실행중입니다! 제발";
    }

    @GetMapping("/test/image/{id}")
    public ResponseEntity<byte[]> getTestImage(@PathVariable Long id) {
        byte[] imageData = testService.getImage(id);

        // 이미지 데이터와 함께 HTTP 200 OK 상태, Content-Type 헤더를 설정하여 반환
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 또는 MediaType.IMAGE_PNG 등 이미지 타입에 맞게 설정
                .body(imageData);
    }
}
