package com.seasonthon.YEIN.gallery.api.dto.request;

import com.seasonthon.YEIN.gallery.domain.MoodTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "갤러리 업로드 요청")
public record GalleryUploadRequest(

        @Schema(description = "필사 제목", example = "오늘의 독서 필사")
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
        String title,

        @Schema(description = "기분 태그 (복수 선택 가능)", example = "[\"CALM\", \"FOCUSED\"]")
        Set<MoodTag> moodTags
) {}
