package com.seasonthon.YEIN.global.oauth.api.dto.response;

import com.seasonthon.YEIN.user.domain.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 응답")
public record UserInfoResponse (
        String email,
        String name,
        String profileImageUrl,
        RoleType roleType
) {}
