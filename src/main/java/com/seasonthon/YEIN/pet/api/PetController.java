package com.seasonthon.YEIN.pet.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.pet.api.dto.request.PetUpdateRequest;
import com.seasonthon.YEIN.pet.api.dto.response.PetStatusResponse;
import com.seasonthon.YEIN.pet.application.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "펫 API", description = "펫 상태 조회 및 변경 API")
public class PetController {

    private final PetService petService;

    @GetMapping("/me")
    @Operation(summary = "내 펫 상태 조회", description = "로그인한 사용자의 펫 이름, 종류, 레벨, 경험치, 진화 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<PetStatusResponse>> getMyPetStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PetStatusResponse response = petService.getPetStatus(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping("/me")
    @Operation(summary = "내 펫 변경", description = "로그인한 사용자의 펫 종류와 이름을 변경합니다. DEFAULT 타입으로는 변경할 수 없습니다.")
    public ResponseEntity<ApiResponse<PetStatusResponse>> updateMyPet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PetUpdateRequest request) {
        PetStatusResponse response = petService.updatePet(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}