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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 작성, 수정, 삭제, 조회")
public class PostController {

    private final PostService postService;

    /**
     * Create a new post.
     *
     * <p>Accepts a validated PostRequest payload and an optional image file. The authenticated user
     * determines the post author.
     *
     * @param request validated post payload (form data)
     * @param image optional image file for the post (may be null)
     * @return ResponseEntity containing an ApiResponse with the created PostResponse and HTTP 201 Created
     */
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

    /**
     * Retrieves a paginated list of posts, optionally filtered by keyword and sorted.
     *
     * <p>Supports optional keyword search and sorting. The authenticated user's id is
     * used to personalize results (e.g., mark whether the user has scrapped or liked posts).
     *
     * @param keyword optional search keyword to filter posts by title/content
     * @param sortBy optional sort mode; supported values: "latest" (newest), "view" (by view count), "scrap" (by scrap count)
     * @param pageable pagination and sorting information
     * @param UserDetails the authenticated user's details (used to derive the requesting user's id)
     * @return a 200 OK ResponseEntity containing an ApiResponse wrapping a Page of PostListResponse
     */
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다. 키워드 검색 및 정렬이 가능합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPosts(
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 방식 (latest: 최신순, view: 조회수순, scrap: 스크랩수순)", example = "latest")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "페이징 정보")
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails UserDetails) {
        Page<PostListResponse> response = postService.getPosts(keyword, sortBy, pageable, UserDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /**
     * Retrieve detailed information for a specific post and increment its view count.
     *
     * @param postId the ID of the post to retrieve
     * @return a 200 OK ResponseEntity carrying an ApiResponse with the PostResponse payload
     */
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다. 조회시 조회수가 증가합니다.")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails UserDetails) {
        PostResponse response = postService.getPostDetail(postId, UserDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /**
     * Update an existing post.
     *
     * Modifies the post identified by {@code postId} using the validated fields in {@code request}.
     * Only the post's author may perform this operation. An optional multipart {@code image} can
     * be supplied to replace or add the post's image.
     *
     * @param postId the ID of the post to update
     * @param request validated post data to apply to the existing post
     * @param image optional image file to attach to the post (may be {@code null})
     * @return ResponseEntity containing an ApiResponse with the updated PostResponse and HTTP 200 OK
     */
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

    /**
     * Delete the post identified by the given ID. Only the post's author is allowed to delete it.
     *
     * @param postId the ID of the post to delete
     * @return a ResponseEntity wrapping an ApiResponse<Void> indicating success (payload is null)
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "작성한 게시글을 삭제합니다. 작성자만 삭제 가능합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /**
     * Retrieves a paginated list of posts authored by the currently authenticated user.
     *
     * Returns an ApiResponse wrapping a Page of PostListResponse with HTTP 200 OK.
     *
     * @param keyword optional search keyword to filter the user's posts
     * @param userDetails authenticated user details; used to identify the current user whose posts are returned
     * @param pageable paging and sorting information for the result page
     * @return ResponseEntity containing ApiResponse&lt;Page&lt;PostListResponse&gt;&gt; with the requested page of posts
     */
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
