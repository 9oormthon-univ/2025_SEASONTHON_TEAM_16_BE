package com.seasonthon.YEIN.comment.api.dto.response;

import com.seasonthon.YEIN.comment.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CommentResponse(
        @Schema(description = "댓글 ID")
        Long id,

        @Schema(description = "댓글 내용")
        String content,

        @Schema(description = "작성자 닉네임")
        String authorNickname,

        @Schema(description = "작성자 프로필 이미지")
        String authorProfileImage,

        @Schema(description = "작성일시")
        LocalDateTime createdAt,

        @Schema(description = "수정일시")
        LocalDateTime updatedAt,

        @Schema(description = "작성자 여부")
        boolean isOwner
) {
    public static CommentResponse from(Comment comment, boolean isOwner) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageUrl(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                isOwner
        );
    }
}
