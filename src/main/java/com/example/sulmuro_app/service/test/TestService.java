package com.example.sulmuro_app.service.test;

import com.example.sulmuro_app.domain.test.Test;
import com.example.sulmuro_app.dto.test.request.TestCreateRequest;
import com.example.sulmuro_app.dto.test.response.TestResponse;
import com.example.sulmuro_app.repository.test.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok 어노테이션
@Service
public class TestService {

    private final TestRepository testRepository;

    @Transactional
    public TestResponse saveTestData(TestCreateRequest request) {
        Test savedTest = testRepository.save(new Test(request.getName()));
        return new TestResponse(savedTest.getName());
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 테스트 데이터가 없습니다. id=" + testId));

        if (test.getImageData() == null) {
            throw new IllegalArgumentException("해당 데이터에 이미지가 없습니다. id=" + testId);
        }
        return test.getImageData();
    }
}
