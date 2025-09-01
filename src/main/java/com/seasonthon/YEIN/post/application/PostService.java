package com.seasonthon.YEIN.post.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.s3.S3UploadService;
import com.seasonthon.YEIN.post.api.dto.request.PostRequest;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.api.dto.response.PostResponse;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.repository.PostRepository;
import com.seasonthon.YEIN.post.domain.repository.ScrapRepository;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public PostResponse createPost(PostRequest request, Long userId, MultipartFile image) {
        User user = getUser(userId);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3UploadService.uploadFile(image);
        }

        Post post = Post.builder()
                .quote(request.quote())
                .author(request.author())
                .imageUrl(imageUrl)
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost, false);
    }

    public Page<PostListResponse> getPosts(String keyword, String sortBy, Pageable pageable, Long userId) {
        String sort = (sortBy != null) ? sortBy : "latest";
        Page<Post> posts = postRepository.findPostsWithFilter(keyword, sort, pageable);

        // 현재 페이지의 모든 게시글 ID 수집
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        // 사용자가 스크랩한 게시글 ID들 조회
        Set<Long> scrapedPostIds = scrapRepository.findScrapedPostIdsByUserIdAndPostIds(userId, postIds);

        // 스크랩 상태를 포함하여 응답 생성
        return posts.map(post -> PostListResponse.from(post, scrapedPostIds.contains(post.getId())));
    }

    @Transactional
    public PostResponse getPostDetail(Long postId, Long userId) {
        Post post = getPost(postId);
        post.incrementViewCount();

        boolean isScraped = scrapRepository.existsByUserIdAndPostId(userId, postId);
        return PostResponse.from(post, isScraped);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, Long userId, MultipartFile image) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        String imageUrl = post.getImageUrl(); // 기존 이미지 URL 유지
        if (image != null && !image.isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (post.getImageUrl() != null) {
                s3UploadService.deleteFile(post.getImageUrl());
            }
            // 새 이미지 업로드
            imageUrl = s3UploadService.uploadFile(image);
        }

        post.updatePost(request.quote(), request.author(), imageUrl);
        boolean isScraped = scrapRepository.existsByUserIdAndPostId(userId, postId);
        return PostResponse.from(post, isScraped);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPost(postId);
        validatePostOwnership(post, userId);

        // 이미지가 있다면 S3에서 삭제
        if (post.getImageUrl() != null) {
            s3UploadService.deleteFile(post.getImageUrl());
        }

        postRepository.delete(post);
    }

    public Page<PostListResponse> getMyPosts(Long userId, String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdWithFilter(userId, keyword, pageable);

        // 현재 페이지의 모든 게시글 ID 수집
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        // 사용자가 스크랩한 게시글 ID들 조회
        Set<Long> scrapedPostIds = scrapRepository.findScrapedPostIdsByUserIdAndPostIds(userId, postIds);

        // 스크랩 상태를 포함하여 응답 생성
        return posts.map(post -> PostListResponse.from(post, scrapedPostIds.contains(post.getId())));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private void validatePostOwnership(Post post, Long userId) {
        if (!post.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}
