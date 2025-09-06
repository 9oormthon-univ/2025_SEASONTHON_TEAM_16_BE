package com.seasonthon.YEIN.pet.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PetNameUpdateRequest(
        @Schema(description = "새로운 펫의 이름", example = "용감한 예인이")
        @NotBlank(message = "펫 이름은 필수입니다.")
        @Size(min = 2, max = 10, message = "펫 이름은 2자 이상 10자 이하로 입력해주세요.")
        String name
) {}