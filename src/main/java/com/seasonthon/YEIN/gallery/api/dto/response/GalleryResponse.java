package com.seasonthon.YEIN.gallery.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "갤러리 목록 응답")
public record GalleryResponse(

        @Schema(description = "갤러리 ID", example = "1")
        Long id,

        @Schema(description = "필사 이미지 S3 URL", example = "https://bucket-name.s3.region.amazonaws.com/uploads/image123.jpg")
        String imageUrl,

        @Schema(description = "총점 (0-100점)", example = "85")
        Integer totalScore,

        @Schema(description = "작성일시", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
