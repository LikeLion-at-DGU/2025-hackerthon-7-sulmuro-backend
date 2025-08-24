package com.example.sulmuro_app.service.article;

import com.example.sulmuro_app.domain.article.Article;
import com.example.sulmuro_app.domain.article.ArticleBlock;
import com.example.sulmuro_app.domain.bin.BlockType;
import com.example.sulmuro_app.dto.article.block.request.BlockCreateRequest;
import com.example.sulmuro_app.dto.article.block.response.BlockResponse;
import com.example.sulmuro_app.repository.article.ArticleBlockRepository;
import com.example.sulmuro_app.domain.article.ArticleRepository;
import com.example.sulmuro_app.service.storage.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ArticleBlockService {

    private final ArticleRepository articleRepository;
    private final ArticleBlockRepository blockRepository;
    private final FileStorageService fileStorageService;

    public ArticleBlockService(ArticleRepository articleRepository,
                               ArticleBlockRepository blockRepository,
                               FileStorageService fileStorageService) {
        this.articleRepository = articleRepository;
        this.blockRepository = blockRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 멀티파트/폼 전용 처리
     * - type: "IMAGE" or "TEXT" (대소문자/공백 허용, 안전 파싱)
     * - position: 정렬 순서
     * - file: IMAGE(파일 업로드)일 때만 사용
     * - data: IMAGE(외부 URL) 또는 TEXT(본문)일 때 사용
     */
    public BlockResponse addBlockMultipart(Long articleId,
                                           String typeStr,
                                           Long position,
                                           MultipartFile file,
                                           String data) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        BlockType type = parseType(typeStr); // 문자열 → Enum 안전 파싱
        String stored = resolveStoredData(articleId, type, file, data);

        ArticleBlock block = new ArticleBlock(article, stored, position, type); // createdAt은 @PrePersist
        return BlockResponse.of(blockRepository.save(block));
    }

    /**
     * JSON 전용 처리 (파일 없음)
     * - type: IMAGE(TEXT URL) 또는 TEXT
     * - data: TEXT 본문 또는 IMAGE의 URL
     */
    public BlockResponse addBlockJson(Long articleId, BlockCreateRequest req) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        BlockType type = parseType(req.getType() == null ? null : req.getType().name());
        // JSON에서는 파일이 없으므로 file=null
        String stored = resolveStoredData(articleId, type, null, req.getData());

        ArticleBlock block = new ArticleBlock(article, stored, req.getPosition(), type);
        return BlockResponse.of(blockRepository.save(block));
    }

    /** 블록 목록 조회 (position ASC, blockId ASC) */
    @Transactional(readOnly = true)
    public List<BlockResponse> listBlocks(Long articleId) {
        return blockRepository
                .findByArticle_ArticleIdOrderByPositionAscBlockIdAsc(articleId)
                .stream()
                .map(BlockResponse::of)
                .toList();
    }

    // ===== 내부 유틸 =====

    /** " image " / "Image" 등 들어와도 안전하게 Enum으로 변환 */
    private BlockType parseType(String typeStr) {
        if (typeStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        try {
            return BlockType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type: " + typeStr);
        }
    }

    /**
     * type/입력 조합에 따라 ArticleBlock.data에 저장할 값을 결정
     * - IMAGE: file 업로드 또는 data(URL) 둘 중 정확히 하나
     * - TEXT: data(본문) 필수, file 금지
     */
    private String resolveStoredData(Long articleId,
                                     BlockType type,
                                     MultipartFile file,
                                     String data) {

        switch (type) {
            case IMAGE -> {
                boolean hasFile = file != null && !file.isEmpty();
                boolean hasData = data != null && !data.isBlank();

                if (!hasFile && !hasData) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "IMAGE: file 또는 data(URL) 중 하나가 필요합니다.");
                }
                if (hasFile && hasData) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "IMAGE: file 과 data(URL)를 동시에 보낼 수 없습니다.");
                }
                // 파일이면 업로드 → URL 반환 / URL이면 그대로 저장
                return hasFile
                        ? fileStorageService.upload(file, "articles/" + articleId)
                        : data.trim();
            }
            case TEXT -> {
                if (file != null && !file.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "TEXT: file은 허용되지 않습니다. data(본문)만 보내세요.");
                }
                if (data == null || data.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "TEXT: data(본문)가 필요합니다.");
                }
                return data;
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 type");
        }
    }
}
