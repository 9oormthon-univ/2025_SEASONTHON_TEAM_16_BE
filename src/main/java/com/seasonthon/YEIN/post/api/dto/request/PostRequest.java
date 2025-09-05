package com.seasonthon.YEIN.post.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "게시글 생성/수정")
public record PostRequest(

        @Schema(description = "게시글 제목", example = "오늘의 명언", required = true)
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "문구 내용", example = "행복은 습관이다.", required = true)
        @NotBlank(message = "문구 내용은 필수입니다.")
        String quote,

        @Schema(description = "저자", example = "허버드")
        String author,

        @Schema(description = "책 제목", example = "긍정의 힘")
        String bookTitle
) {}
