package com.seasonthon.YEIN.ai.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "손글씨 분석 결과")
public record HandwritingAnalysisResponse(

        @Schema(description = "정렬 점수", example = "25", minimum = "0", maximum = "33")
        int alignmentScore,

        @Schema(description = "간격 점수", example = "28", minimum = "0", maximum = "33")
        int spacingScore,

        @Schema(description = "일관성 점수", example = "30", minimum = "0", maximum = "34")
        int consistencyScore,

        @Schema(description = "총점", example = "83", minimum = "0", maximum = "100")
        int totalScore,

        @Schema(description = "AI 피드백 및 개선사항")
        String feedback,

        @Schema(description = "상세 분석 내용")
        String detailedAnalysis

) {
    public static HandwritingAnalysisResponse createDefault() {
        return new HandwritingAnalysisResponse(
                15, 15, 15, 45,
                "분석 중 오류가 발생했습니다.",
                "AI 응답을 파싱할 수 없습니다."
        );
    }
}
