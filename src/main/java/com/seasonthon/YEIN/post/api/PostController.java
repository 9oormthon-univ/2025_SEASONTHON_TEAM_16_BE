package com.seasonthon.YEIN.post.api;

import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.post.api.dto.request.PostRequest;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.api.dto.response.PostResponse;
import com.seasonthon.YEIN.post.application.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 작성, 수정, 삭제, 조회")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestPart("data") @Valid PostRequest request,
            @Parameter(description = "이미지 파일 (선택사항)", schema = @Schema(type = "string", format = "binary"))
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponse response = postService.createPost(request, userDetails.getUserId(), image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다. 키워드 검색 및 정렬이 가능합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPosts(
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 방식 (latest: 최신순, view: 조회수순, scrap: 스크랩수순)", example = "latest")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "페이징 정보")
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<PostListResponse> response = postService.getPosts(keyword, sortBy, pageable, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다. 조회시 조회수가 증가합니다.")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails UserDetails) {
        PostResponse response = postService.getPostDetail(postId, UserDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping(value = "/{postId}", consumes = "multipart/form-data")
    @Operation(summary = "게시글 수정", description = "작성한 게시글을 수정합니다. 작성자만 수정 가능합니다.")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @RequestPart("data") @Valid PostRequest request,
            @Parameter(description = "이미지 파일 (선택사항)", schema = @Schema(type = "string", format = "binary"))
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponse response = postService.updatePost(postId, request, userDetails.getUserId(), image);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "작성한 게시글을 삭제합니다. 작성자만 삭제 가능합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/my")
    @Operation(summary = "내 게시글 목록 조회", description = "로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getMyPosts(
            @Parameter(description = "검색 키워드", example = "행복")
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        Page<PostListResponse> response = postService.getMyPosts(userDetails.getUserId(), keyword, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
