package com.seasonthon.YEIN.pet.api.dto.request;

import com.seasonthon.YEIN.pet.domain.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PetUpdateRequest(
        @Schema(description = "변경할 펫의 종류", example = "TYPE_1")
        @NotNull(message = "펫 타입을 지정해야 합니다.")
        PetType petType
) {}
