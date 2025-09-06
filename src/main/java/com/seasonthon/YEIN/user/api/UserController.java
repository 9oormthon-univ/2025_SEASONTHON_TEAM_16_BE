package com.seasonthon.YEIN.user.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.application.PostService;
import com.seasonthon.YEIN.user.api.dto.request.UpdateProfileRequest;
import com.seasonthon.YEIN.user.api.dto.response.UpdateProfileResponse;
import com.seasonthon.YEIN.user.api.dto.response.UserProfileResponse;
import com.seasonthon.YEIN.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PostService postService;


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

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다. 이메일, 총 필사 수, 평균 필사 점수 등을 포함합니다.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse response = userService.getUserProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/me/liked-posts")
    @Operation(summary = "내가 추천한 게시글 목록 조회", description = "로그인한 사용자가 추천(좋아요)한 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<PostListResponse> response = postService.getLikedPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));

    }
}