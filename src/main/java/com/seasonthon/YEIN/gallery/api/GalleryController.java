package com.seasonthon.YEIN.gallery.api;

import com.seasonthon.YEIN.gallery.api.dto.response.GalleryDetailResponse;
import com.seasonthon.YEIN.gallery.api.dto.response.GalleryResponse;
import com.seasonthon.YEIN.gallery.application.GalleryService;
import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/galleries")
@RequiredArgsConstructor
@Tag(name = "필사 갤러리 API", description = "AI 분석 후 업로드 된 사진 및 분석 자료를 조회")
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    @Operation(summary = "필사 갤러리 목록 조회", description = "사용자의 필사 갤러리를 기간별,점수별로 필터링하여 페이징 조회합니다.")
    public ResponseEntity<ApiResponse<Page<GalleryResponse>>> getGalleries(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "기간 필터링 옵션: week(최근 7일), month(최근 30일), all(전체 기간, 생략시 기본값)", example = "week")
            @RequestParam(required = false) String period,

            @Parameter(description = "최소 점수 (0-100)", example = "70")
            @RequestParam(required = false) Integer minScore,

            @Parameter(description = "최대 점수 (0-100)", example = "100")
            @RequestParam(required = false) Integer maxScore,

            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GalleryResponse> galleries = galleryService.getGalleriesWithFilters(userDetails.getUserId(), period, minScore, maxScore, pageable);

        return ResponseEntity.ok(ApiResponse.onSuccess(galleries));
    }

    @GetMapping("/{galleryId}")
    @Operation(summary = "필사 갤러리 상세 조회", description = "특정 갤러리 항목의 상세 정보(점수, 피드백, 분석 결과)를 조회합니다.")
    public ResponseEntity<ApiResponse<GalleryDetailResponse>> getGalleryDetail(
            @Parameter(description = "조회할 갤러리 ID", example = "1")
            @PathVariable Long galleryId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        GalleryDetailResponse gallery = galleryService.getGalleryDetail(galleryId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(gallery));
    }
}
