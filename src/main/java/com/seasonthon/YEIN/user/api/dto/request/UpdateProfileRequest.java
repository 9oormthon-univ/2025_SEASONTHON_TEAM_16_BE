package com.seasonthon.YEIN.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 프로필 수정 요청")
public record UpdateProfileRequest(
        
        @Schema(description = "닉네임", example = "새로운닉네임")
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        String nickname
) {
}