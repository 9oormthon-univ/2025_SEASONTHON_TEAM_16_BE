package com.seasonthon.YEIN.post.api.dto.response;

import com.seasonthon.YEIN.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게시글 응답")
public record PostResponse(

        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "게시글 제목", example = "오늘의 명언")
        String title,

        @Schema(description = "문구 내용", example = "행복은 습관이다.")
        String quote,

        @Schema(description = "저자", example = "허버드")
        String author,

        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "조회수", example = "127")
        Long viewCount,

        @Schema(description = "스크랩수", example = "23")
        Long scrapCount,

        @Schema(description = "현재 사용자의 스크랩 여부", example = "true")
        Boolean isScraped,

        @Schema(description = "작성자 닉네임", example = "글귀러버")
        String createdByNickname,

        @Schema(description = "추천수", example = "23")
        Integer likeCount,

        @Schema(description = "현재 사용자의 추천 여부", example = "true")
        Boolean isLiked,

        @Schema(description = "생성일시", example = "2024-03-15T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일시", example = "2024-03-15T15:45:00")
        LocalDateTime updatedAt
) {

    public static PostResponse from(Post post, Boolean isScraped, Boolean isLiked) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getQuote(),
                post.getAuthor(),
                post.getImageUrl(),
                post.getViewCount(),
                post.getScrapCount(),
                isScraped,
                post.getUser().getNickname(),
                post.getLikeCount(),
                isLiked,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
