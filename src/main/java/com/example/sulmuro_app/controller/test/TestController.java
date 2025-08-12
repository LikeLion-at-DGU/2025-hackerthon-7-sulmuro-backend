package com.example.sulmuro_app.controller.test;

import com.example.sulmuro_app.dto.test.request.TestCreateRequest;
import com.example.sulmuro_app.dto.test.response.TestResponse;
import com.example.sulmuro_app.service.test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor // Lombok 어노테이션
@RestController
public class TestController {
    private final TestService testService;


    @PostMapping("/test")
    public TestResponse saveTestData(@RequestBody TestCreateRequest request) {
        return testService.saveTestData(request);
    }
}
