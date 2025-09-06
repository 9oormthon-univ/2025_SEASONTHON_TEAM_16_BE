package com.seasonthon.YEIN.comment.api;

import com.seasonthon.YEIN.comment.api.dto.request.CommentRequest;
import com.seasonthon.YEIN.comment.api.dto.response.CommentResponse;
import com.seasonthon.YEIN.comment.api.dto.response.MyCommentResponse;
import com.seasonthon.YEIN.comment.application.CommentService;
import com.seasonthon.YEIN.global.code.dto.ApiResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Long>> createComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long commentId = commentService.createComment(postId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(commentId));
    }

    @Operation(summary = "게시글 댓글 조회", description = "특정 게시글의댓글 목록을 조회합니다.")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>>
    findCommentsByPost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pageable pageable = PageRequest.of(0, 20);
        Long currentUserId = userDetails != null ? userDetails.getUserId() : null;

        Page<CommentResponse> comments = commentService.findCommentsByPost(postId, pageable, currentUserId);
        return ResponseEntity.ok(ApiResponse.onSuccess(comments));
    }

    @Operation(summary = "내 댓글 조회", description = "내가 작성한 댓글 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<MyCommentResponse>>> findMyComments(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MyCommentResponse> comments = commentService.findMyComments(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(comments));
    }

    @Operation(summary = "댓글 수정", description = "내가 작성한 댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.updateComment(commentId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
