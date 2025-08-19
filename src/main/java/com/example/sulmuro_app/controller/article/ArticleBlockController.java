package com.example.sulmuro_app.controller.article;

import com.example.sulmuro_app.domain.bin.BlockType;
import com.example.sulmuro_app.dto.article.block.request.BlockCreateRequest;
import com.example.sulmuro_app.dto.article.block.response.BlockResponse;
import com.example.sulmuro_app.dto.bin.ApiResponse;
import com.example.sulmuro_app.service.article.ArticleBlockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles/{articleId}/blocks")
public class ArticleBlockController {

    private final ArticleBlockService blockService;

    public ArticleBlockController(ArticleBlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public ApiResponse<BlockResponse> addMultipart(
            @PathVariable Long articleId,
            @RequestParam("type") String typeStr,          // 🔸 문자열로 받기
            @RequestParam("position") Long position,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "data", required = false) String data
    ) {
        // 디버그: 실제 들어온 원시값 확인
        System.out.println("[REQ] typeStr=" + typeStr + ", pos=" + position +
                ", filePresent=" + (file != null && !file.isEmpty()) +
                ", dataPresent=" + (data != null && !data.isBlank()));

        BlockResponse res = blockService.addBlockMultipart(articleId, typeStr, position, file, data);
        return ApiResponse.success("블록이 추가되었습니다.", res);
    }

    /** 2) JSON 전용: 파일 없이 TEXT/IMAGE-URL 을 JSON으로 보낼 때 */
    @PostMapping(path = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<BlockResponse> addJson(
            @PathVariable Long articleId,
            @RequestBody BlockCreateRequest req
    ) {
        BlockResponse res = blockService.addBlockJson(articleId, req);
        return ApiResponse.success("블록이 추가되었습니다.", res);
    }


    @GetMapping
    public ApiResponse<List<BlockResponse>> list(@PathVariable Long articleId) {
        List<BlockResponse> res = blockService.listBlocks(articleId);
        return ApiResponse.success("블록 목록을 불러왔습니다.", res);
    }
}
