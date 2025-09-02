package com.seasonthon.YEIN.recommendation.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.recommendation.api.dto.response.TodayQuoteResponse;
import com.seasonthon.YEIN.recommendation.application.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "문구 추천 API", description = "매일 랜덤 문구를 추천하는 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "오늘의 문구 조회", description = "매일 자정에 갱신되는 오늘의 추천 문구를 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayQuoteResponse>> getTodayQuote() {
        TodayQuoteResponse response = recommendationService.findTodayQuote();
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
