package com.seasonthon.YEIN.post.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.code.status.SuccessStatus;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.post.application.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
