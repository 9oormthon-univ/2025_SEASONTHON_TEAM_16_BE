package com.seasonthon.YEIN.post.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.s3.S3UploadService;
import com.seasonthon.YEIN.post.api.dto.request.PostRequest;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.api.dto.response.PostResponse;
import com.seasonthon.YEIN.post.domain.Like;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.repository.LikeRepository;
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

import java.util.Collections;
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
    private final LikeRepository likeRepository;

    @Transactional
    public PostResponse createPost(PostRequest request, Long userId, MultipartFile image) {
        User user = getUser(userId);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3UploadService.uploadFile(image);
        }

        Post post = Post.builder()
                .title(request.title())
                .quote(request.quote())
                .author(request.author())
                .bookTitle(request.bookTitle())
                .imageUrl(imageUrl)
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost, false, false);
    }

    public Page<PostListResponse> getPosts(String keyword, String sortBy, Pageable pageable, Long userId) {
        String sort = (sortBy != null) ? sortBy : "latest";
        Page<Post> posts = postRepository.findPostsWithFilter(keyword, sort, pageable);

        if (userId == null) {
            // 비인가 사용자: 스크랩/라이크 상태 모두 false
            return posts.map(post -> PostListResponse.from(post, false, false));
        }

        // 인가 사용자: 스크랩/라이크 상태 조회
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        Set<Long> scrapedPostIds = scrapRepository.findScrapedPostIdsByUserIdAndPostIds(userId, postIds);
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);

        // 스크랩 상태를 포함하여 응답 생성
        return posts.map(post -> PostListResponse.from(post, scrapedPostIds.contains(post.getId()), likedPostIds.contains(post.getId())));
    }

    @Transactional
    public PostResponse getPostDetail(Long postId, Long userId) {
        postRepository.incrementViewCount(postId); // 동시성 문제 해결을 위해 DB에서 직접 조회
        Post post = getPost(postId);

        boolean isScraped = false;
        boolean isLiked = false;

        // 인가 사용자일 때만 스크랩/추천 상태 조회
        if (userId != null) {
            isScraped = scrapRepository.existsByUserIdAndPostId(userId, postId);
            isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);
        }

        return PostResponse.from(post, isScraped, isLiked);
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

        post.updatePost(request.title(), request.quote(), request.author(), imageUrl, request.bookTitle());
        boolean isScraped = scrapRepository.existsByUserIdAndPostId(userId, postId);
        boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);
        return PostResponse.from(post, isScraped, isLiked);
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

    @Transactional
    public void toggleLike(Long postId, Long userId) {
        Post post = getPost(postId);
        User user = getUser(userId);

        boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);

        if (isLiked) {
            likeRepository.deleteByUserIdAndPostId(userId, postId);
            post.decrementLikeCount();
        } else {
            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
            post.incrementLikeCount();
        }
    }

    public Page<PostListResponse> getMyPosts(Long userId, String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdWithFilter(userId, keyword, pageable);

        // 현재 페이지의 모든 게시글 ID 수집
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        // 사용자가 스크랩한 게시글 ID들 조회
        Set<Long> scrapedPostIds = scrapRepository.findScrapedPostIdsByUserIdAndPostIds(userId, postIds);
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);

        // 스크랩 상태를 포함하여 응답 생성
        return posts.map(post -> PostListResponse.from(post, scrapedPostIds.contains(post.getId()), likedPostIds.contains(post.getId())));
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

    public Page<PostListResponse> getLikedPosts(Long userId, Pageable pageable) {
        Page<Post> posts = likeRepository.findLikedPostsByUserId(userId, pageable);
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();
        Set<Long> scrapedPostIds = scrapRepository.findScrapedPostIdsByUserIdAndPostIds(userId, postIds);
        return posts.map(post -> PostListResponse.from(post, scrapedPostIds.contains(post.getId()), true));
    }

    public Page<PostListResponse> getScrapedPosts(Long userId, Pageable pageable) {
        Page<Post> posts = scrapRepository.findScrapedPostsByUserIdWithFilter(userId, null, "scrap_date", pageable);
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
        return posts.map(post -> PostListResponse.from(post, true, likedPostIds.contains(post.getId())));
    }
}
