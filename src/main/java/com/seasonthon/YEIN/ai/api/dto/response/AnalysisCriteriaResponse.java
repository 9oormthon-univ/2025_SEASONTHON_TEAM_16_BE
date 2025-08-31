package com.seasonthon.YEIN.ai.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "손글씨 분석 기준 정보")
public record AnalysisCriteriaResponse(

        @Schema(description = "분석 기준 설명", example = "손글씨 분석은 4가지 기준으로 평가됩니다:")
        String description,

        @Schema(description = "정렬 평가 기준")
        CriteriaItemResponse alignment,

        @Schema(description = "간격 평가 기준")
        CriteriaItemResponse spacing,

        @Schema(description = "일관성 평가 기준")
        CriteriaItemResponse consistency,

        @Schema(description = "길이 평가 기준")
        CriteriaItemResponse length,

        @Schema(description = "총점 설명", example = "총 100점 만점으로 평가되며, 각 기준별로 25점씩 배점됩니다.")
        String totalScoreInfo

) {
    public static AnalysisCriteriaResponse createDefault() {
        return new AnalysisCriteriaResponse(
                "손글씨 분석은 4가지 기준으로 평가됩니다:",
                new CriteriaItemResponse("정렬", "글자들이 베이스라인에 정렬된 정도", 25),
                new CriteriaItemResponse("간격", "글자 사이의 간격이 일정한 정도", 25),
                new CriteriaItemResponse("일관성", "글자 크기가 균등한 정도", 25),
                new CriteriaItemResponse("길이", "필사한 글자의 양 (많을수록 높은 점수)", 25),
                "총 100점 만점으로 평가되며, 각 기준별로 25점씩 배점됩니다."
        );
    }
}
