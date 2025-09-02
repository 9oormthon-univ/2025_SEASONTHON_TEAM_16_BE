package com.seasonthon.YEIN.gallery.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "갤러리 상세 정보 응답")
public record GalleryDetailResponse(

        @Schema(description = "갤러리 ID", example = "1")
        Long id,

        @Schema(description = "필사 이미지 S3 URL", example = "https://bucket-name.s3.region.amazonaws.com/uploads/image123.jpg")
        String imageUrl,

        @Schema(description = "정렬 점수 (0-25점)", example = "22")
        Integer alignmentScore,

        @Schema(description = "간격 점수 (0-25점)", example = "20")
        Integer spacingScore,

        @Schema(description = "일관성 점수 (0-25점)", example = "23")
        Integer consistencyScore,

        @Schema(description = "길이 점수 (0-25점)", example = "20")
        Integer lengthScore,

        @Schema(description = "총점 (0-100점)", example = "85")
        Integer totalScore,

        @Schema(description = "AI 피드백", example = "전체적으로 정렬이 잘 되어 있고, 글자 간격도 균등합니다. 일관성 있는 글씨체로 작성하셨네요.")
        String feedback,

        @Schema(description = "잘한점과 칭찬", example = "간격이 잘 맞습니다.")
        String strengths,

        @Schema(description = "필사 내용 분석", example = "이 글은 자기계발에 관한 내용으로, 긍정적인 마인드셋의 중요성을 강조하고 있습니다.")
        String detailedAnalysis,

        @Schema(description = "작성일시", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
