package com.seasonthon.YEIN.gallery.api.dto.response;

import com.seasonthon.YEIN.gallery.domain.MoodTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "갤러리 목록 응답")
public record GalleryResponse(

        @Schema(description = "갤러리 ID", example = "1")
        Long id,

        @Schema(description = "필사 제목", example = "오늘의 독서 필사")
        String title,

        @Schema(description = "기분 태그", example = "[\"CALM\", \"JOY\"]")
        Set<MoodTag> moodTags,

        @Schema(description = "필사 이미지 S3 URL", example = "https://bucket-name.s3.region.amazonaws.com/uploads/image123.jpg")
        String imageUrl,

        @Schema(description = "총점 (0-100점)", example = "85")
        Integer totalScore,

        @Schema(description = "작성일시", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt
) {}
