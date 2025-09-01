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

    /**
     * Creates a new post for the specified user, optionally uploading an attached image.
     *
     * The method looks up the user, uploads the provided image to S3 (if present), builds and
     * persists a Post with the request's quote and author, and returns a PostResponse for the
     * saved post. The returned response's `isScraped` flag is set to false.
     *
     * @param request DTO containing post fields (quote, author)
     * @param userId  ID of the user who will own the post
     * @param image   optional image file to upload and attach to the post; may be null or empty
     * @return a PostResponse representing the persisted post (with `isScraped == false`)
     * @throws GeneralException if the user with the given ID does not exist (USER_NOT_FOUND)
     */
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

    /**
     * Retrieve a page of posts filtered by keyword and sorted, with per-post scrap status for the given user.
     *
     * The method applies an optional text filter and sorting (defaults to "latest" when `sortBy` is null),
     * loads the requested page, determines which posts on that page the user has scraped, and returns
     * a mapped Page of PostListResponse where each item includes an `isScraped` flag.
     *
     * @param keyword optional text filter applied to posts
     * @param sortBy  optional sort key (defaults to "latest" when null)
     * @param pageable pagination and page size information
     * @param userId  id of the user used to determine whether each post is scraped
     * @return a page of PostListResponse objects with the `isScraped` flag set per post
     */
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

    /**
     * Returns detailed information for a post and increments its view count.
     *
     * Retrieves the Post by id (throws if not found), increments and persists its view count,
     * determines whether the specified user has scraped the post, and returns a PostResponse
     * that includes the scraped flag.
     *
     * @param postId the id of the post to retrieve
     * @param userId the id of the user used to determine scrap status
     * @return a PostResponse containing the post details and whether the user has scraped it
     * @throws com.seasonthon.YEIN.common.exception.GeneralException if the post is not found (ErrorStatus.POST_NOT_FOUND)
     */
    @Transactional
    public PostResponse getPostDetail(Long postId, Long userId) {
        Post post = getPost(postId);
        post.incrementViewCount();

        boolean isScraped = scrapRepository.existsByUserIdAndPostId(userId, postId);
        return PostResponse.from(post, isScraped);
    }

    /**
     * Updates an existing post if the requesting user is the post owner.
     *
     * If a non-empty image is provided, the current image (if any) is deleted from S3 and the new image is uploaded.
     * The post's quote, author, and imageUrl are updated and an updated PostResponse is returned that includes
     * whether the post is scraped by the requesting user.
     *
     * @param postId the ID of the post to update
     * @param request DTO containing the new quote and author values
     * @param userId the ID of the user performing the update (must be the post owner)
     * @param image optional new image file; when provided replaces the existing image
     * @return a PostResponse representing the updated post and the requesting user's scrap state for that post
     * @throws GeneralException if the post does not exist or the user is not the post owner
     */
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

    /**
     * Deletes a post owned by the given user.
     *
     * If the post has an associated image URL, the image is deleted from S3 before the post is removed from the repository.
     *
     * @param postId the ID of the post to delete
     * @param userId the ID of the user attempting the deletion (must be the post owner)
     * @throws GeneralException if the post does not exist (POST_NOT_FOUND) or the user is not the post owner (FORBIDDEN)
     */
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

    /**
     * Retrieves a paginated list of posts authored by the given user, including per-post scrap state for that user.
     *
     * Retrieves posts filtered by the optional keyword and mapped to PostListResponse objects; each response
     * includes an `isScraped` flag that is true when the requesting user has scraped that post.
     *
     * @param userId   the ID of the author whose posts to retrieve
     * @param keyword  optional text filter applied to the user's posts (may be null or empty to disable filtering)
     * @param pageable pagination and sorting information for the returned page
     * @return a page of PostListResponse DTOs for the user's posts, each annotated with whether it is scraped by the user
     */
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

    /**
     * Retrieve the User with the given ID.
     *
     * @param userId the ID of the user to fetch
     * @return the found User
     * @throws GeneralException with ErrorStatus.USER_NOT_FOUND if no user exists for the given ID
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    /**
     * Retrieves a Post by its id.
     *
     * @param postId the id of the post to retrieve
     * @return the Post with the given id
     * @throws GeneralException if no post exists with the provided id (ErrorStatus.POST_NOT_FOUND)
     */
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    /**
     * Ensures the given user is the owner of the post.
     *
     * @param post   the post whose ownership is being validated
     * @param userId the id of the user to check against the post's owner
     * @throws GeneralException if the user is not the post's owner (ErrorStatus.FORBIDDEN)
     */
    private void validatePostOwnership(Post post, Long userId) {
        if (!post.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}
