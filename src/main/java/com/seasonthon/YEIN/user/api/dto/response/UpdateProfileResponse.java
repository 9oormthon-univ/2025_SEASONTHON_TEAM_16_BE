package com.seasonthon.YEIN.user.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 수정 응답")
public record UpdateProfileResponse(
        
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        
        @Schema(description = "닉네임", example = "새로운닉네임")
        String nickname,
        
        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl
) {
    public static UpdateProfileResponse from(Long userId, String nickname, String profileImageUrl) {
        return new UpdateProfileResponse(userId, nickname, profileImageUrl);
    }
}