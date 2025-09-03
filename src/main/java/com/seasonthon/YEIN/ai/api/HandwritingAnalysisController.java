package com.seasonthon.YEIN.ai.api;

import com.seasonthon.YEIN.ai.api.dto.response.AnalysisCriteriaResponse;
import com.seasonthon.YEIN.ai.api.dto.response.HandwritingAnalysisResponse;
import com.seasonthon.YEIN.ai.application.HandwritingAnalysisService;
import com.seasonthon.YEIN.gallery.api.dto.request.GalleryUploadRequest;
import com.seasonthon.YEIN.gallery.domain.MoodTag;
import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Tag(name = "필사 이미지 분석 API", description = "AI를 사용하여 필사 이미지 분석 및 점수 평가")
@RestController
@RequestMapping("/api/handwriting")
@RequiredArgsConstructor
public class HandwritingAnalysisController {

    private final HandwritingAnalysisService analysisService;

    @Operation(summary = "필사 이미지 분석", description = "업로드된 필사 이미지를 AI가 분석하여 정렬, 간격, 일관성, 가독성을 기준으로 점수를 매깁니다. " +
            "지원 형식: JPG, JPEG, PNG, WEBP (최대 10MB) 업로드 후에 필사 갤러리에 사진 및 분석 결과가 저장됩니다.")
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HandwritingAnalysisResponse>> analyzeHandwriting(
            @Parameter(description = "분석할 손글씨 이미지 파일 (JPG, PNG, WEBP 지원, 최대 10MB)", required = true)
            @RequestPart("image") MultipartFile image,
            @Parameter(description = "갤러리 업로드 정보", required = true)
            @Valid @RequestPart("data") GalleryUploadRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        HandwritingAnalysisResponse response = analysisService.analyzeHandwriting(image, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "분석 기준 정보 조회", description = "필사 이미지 분석에 사용되는 평가 기준과 점수 체계를 조회합니다.")
    @GetMapping("/criteria")
    public ResponseEntity<ApiResponse<AnalysisCriteriaResponse>> getAnalysisCriteria() {
        AnalysisCriteriaResponse criteria = AnalysisCriteriaResponse.createDefault();
        return ResponseEntity.ok(ApiResponse.onSuccess(criteria));
    }
}
