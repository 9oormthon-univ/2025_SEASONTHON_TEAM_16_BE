package com.seasonthon.YEIN.ai.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "손글씨 분석 결과")
public record HandwritingAnalysisResponse(

        @Schema(description = "정렬 점수", example = "20", minimum = "0", maximum = "25")
        int alignmentScore,

        @Schema(description = "간격 점수", example = "22", minimum = "0", maximum = "25")
        int spacingScore,

        @Schema(description = "일관성 점수", example = "24", minimum = "0", maximum = "25")
        int consistencyScore,

        @Schema(description = "길이 점수", example = "23", minimum = "0", maximum = "25")
        int lengthScore,

        @Schema(description = "총점", example = "89", minimum = "0", maximum = "100")
        int totalScore,

        @Schema(description = "AI 피드백 및 개선사항")
        String feedback,

        @Schema(description = "필사한 글의 의미 분석")
        String detailedAnalysis

) {
    public static HandwritingAnalysisResponse createDefault() {
        return new HandwritingAnalysisResponse(
                12, 12, 12, 12, 48,
                "분석 중 오류가 발생했습니다.",
                "글의 의미를 분석할 수 없습니다."
        );
    }
}
