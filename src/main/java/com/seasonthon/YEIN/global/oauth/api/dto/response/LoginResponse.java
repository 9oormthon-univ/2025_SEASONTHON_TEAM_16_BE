package com.seasonthon.YEIN.global.oauth.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse (
        @Schema(description = "accessToken 값")
        String accessToken,

        @Schema(description = "refreshToken 값")
        String refreshToken
) {}
