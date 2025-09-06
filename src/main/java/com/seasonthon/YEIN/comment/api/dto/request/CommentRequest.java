package com.seasonthon.YEIN.comment.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @Schema(description = "댓글 내용", example = "좋은 글이네요!")
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 1000, message = "댓글은 1000자를 넘을 수 없습니다.")
        String content
) {
    public static CommentRequest of(String content) {
        return new CommentRequest(content);
    }
}
