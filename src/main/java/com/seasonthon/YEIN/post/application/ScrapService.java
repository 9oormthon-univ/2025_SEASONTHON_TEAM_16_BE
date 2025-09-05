package com.seasonthon.YEIN.post.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.post.api.dto.response.PostListResponse;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.Scrap;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public boolean toggleScrap(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        Optional<Scrap> existingScrap = scrapRepository.findByUserIdAndPostId(userId, postId);

        if (existingScrap.isPresent()) {
            // 스크랩 취소
            scrapRepository.delete(existingScrap.get());
            post.decrementScrapCount();
            return false;
        } else {
            // 스크랩 추가
            Scrap scrap = Scrap.builder()
                    .user(user)
                    .post(post)
                    .build();
            scrapRepository.save(scrap);
            post.incrementScrapCount();
            return true;
        }
    }

    public Page<PostListResponse> getScrapedPosts(Long userId, String keyword, String sortBy, Pageable pageable) {
        String sort = (sortBy != null) ? sortBy : "latest";
        Page<Post> scrapedPosts = scrapRepository.findScrapedPostsByUserIdWithFilter(userId, keyword, sort, pageable);

        // 현재 페이지의 모든 게시글 ID 수집
        List<Long> postIds = scrapedPosts.getContent().stream()
                .map(Post::getId)
                .toList();

        // 사용자가 좋아요한 게시글 ID들 조회 (스크랩은 이미 모두 true)
        Set<Long> likedPostIds = likeRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);

        return scrapedPosts.map(post -> PostListResponse.from(post, true, likedPostIds.contains(post.getId())));
    }

    public boolean isScraped(Long userId, Long postId) {
        return scrapRepository.existsByUserIdAndPostId(userId, postId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }
}
