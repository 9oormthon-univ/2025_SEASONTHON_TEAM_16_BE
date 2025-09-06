package com.seasonthon.YEIN.comment.api.dto.response;

import com.seasonthon.YEIN.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MyCommentResponse(
        @Schema(description = "댓글 ID")
        Long commentId,

        @Schema(description = "댓글 내용")
        String content,

        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "게시글 제목")
        String postTitle,

        @Schema(description = "작성일시")
        LocalDateTime createdAt
) {
    public static MyCommentResponse from(Comment comment) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getPost().getTitle(),
                comment.getCreatedAt()
        );
    }
}
