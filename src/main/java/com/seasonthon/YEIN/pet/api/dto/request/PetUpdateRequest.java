package com.seasonthon.YEIN.pet.api.dto.request;

import com.seasonthon.YEIN.pet.domain.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PetUpdateRequest(
        @Schema(description = "변경할 펫의 종류")
        @NotNull(message = "펫 타입을 지정해야 합니다.")
        PetType petType,

        @Schema(description = "새로운 펫의 이름", example = "용감한 예인이")
        @NotBlank(message = "펫 이름은 필수입니다.")
        @Size(min = 2, max = 10, message = "펫 이름은 2자 이상 10자 이하로 입력해주세요.")
        String name
) {}
