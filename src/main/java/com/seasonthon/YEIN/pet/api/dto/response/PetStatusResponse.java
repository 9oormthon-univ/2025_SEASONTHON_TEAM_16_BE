package com.seasonthon.YEIN.pet.api.dto.response;

import com.seasonthon.YEIN.pet.domain.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PetStatusResponse(

        @Schema(description = "펫 이름", example = "예인이")
        String name,

        @Schema(description = "펫 종류")
        PetType petType,

        @Schema(description = "현재 레벨", example = "5")
        int level,

        @Schema(description = "현재 경험치", example = "150")
        int currentXp,

        @Schema(description = "다음 레벨까지 필요한 경험치", example = "200")
        int xpToNextLevel, // 다음 레벨까지 필요한 경험치

        @Schema(description = "진화 단계", example = "1")
        int evolutionStage // 진화 단계
) {}
