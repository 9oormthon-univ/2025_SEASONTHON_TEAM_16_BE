package com.seasonthon.YEIN.user.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.user.api.dto.request.UpdateProfileRequest;
import com.seasonthon.YEIN.user.api.dto.response.UpdateProfileResponse;
import com.seasonthon.YEIN.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 API", description = "사용자 프로필 관리")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 프로필 수정", description = "닉네임과 프로필 이미지를 선택적으로 수정합니다.")
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart(value = "data", required = false) UpdateProfileRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage) {
        
        String nickname = request != null ? request.nickname() : null;
        
        UpdateProfileResponse response = userService.updateProfile(userDetails.getUserId(), nickname, profileImage);
        
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
