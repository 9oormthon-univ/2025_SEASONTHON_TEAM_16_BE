package com.seasonthon.YEIN.user.api.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        Long userId,
        String email,
        String nickname,
        String profileImageUrl,
        double averageHandwritingScore, // 평균 필사 점수
        int totalGalleries, // 총 필사 수
        int todayGalleries // 오늘 작성한 필사 수
) {}
