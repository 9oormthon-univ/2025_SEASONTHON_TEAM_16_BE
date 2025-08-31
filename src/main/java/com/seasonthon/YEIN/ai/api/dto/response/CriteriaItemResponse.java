package com.seasonthon.YEIN.ai.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "분석 기준 항목")
public record CriteriaItemResponse(

        @Schema(description = "평가 항목명", example = "정렬 상태")
        String name,

        @Schema(description = "평가 기준 설명", example = "글자들이 베이스라인에 정렬된 정도")
        String description,

        @Schema(description = "최대 점수", example = "25", minimum = "0", maximum = "25")
        int maxScore
) {}
