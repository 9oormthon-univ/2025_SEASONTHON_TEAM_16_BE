package com.seasonthon.YEIN.post.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.code.status.SuccessStatus;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.application.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
@Tag(name = "스크랩 API", description = "게시글 스크랩")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/{postId}")
    @Operation(summary = "게시글 스크랩 토글", description = "게시글을 스크랩하거나 스크랩을 취소합니다.")
    public ResponseEntity<ApiResponse<Boolean>> toggleScrap(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isScraped = scrapService.toggleScrap(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(isScraped));
    }

    @GetMapping("/posts")
    @Operation(summary = "스크랩한 게시글 목록 조회", description = "사용자가 스크랩한 게시글 목록을 조회합니다. 키워드 검색 및 정렬이 가능합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getScrapedPosts(
            @Parameter(description = "검색 키워드", example = "행복")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 방식 (latest: 최신순, view:조회수순, scrap: 스크랩수순, like: 추천수순, scrap_date: 스크랩날짜순)", example = "latest")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "페이징 정보") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<PostListResponse> response = scrapService.getScrapedPosts(userDetails.getUserId(), keyword, sortBy, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
