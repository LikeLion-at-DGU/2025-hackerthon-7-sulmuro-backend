package com.example.sulmuro_app.controller.translate;


import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.dto.translate.request.TranslateRequest;
import com.example.sulmuro_app.dto.translate.response.TranslateRecommendResponse;
import com.example.sulmuro_app.dto.translate.response.TranslateResponse;
import com.example.sulmuro_app.service.translate.TranslateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/translate")
@RequiredArgsConstructor
public class TranslateController {

    private final TranslateService translateService;

    @PostMapping
    public ApiResponse<TranslateResponse> translateText(
            @Valid @RequestBody TranslateRequest request
    ) {
        TranslateResponse response = translateService.translate(request);
        return ApiResponse.success("번역이 완료되었습니다.", response);
    }


    @PostMapping("/recommend")
    public ApiResponse<TranslateRecommendResponse> translateTextWithRecommend(
            @Valid @RequestBody TranslateRequest request
    ) {
        TranslateRecommendResponse response = translateService.translateWithRecommend(request);
        return ApiResponse.success("번역 및 추천 표현 조회가 완료되었습니다.", response);
    }
}
