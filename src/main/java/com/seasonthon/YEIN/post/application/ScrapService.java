package com.seasonthon.YEIN.post.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.Scrap;
import com.seasonthon.YEIN.post.domain.repository.PostRepository;
import com.seasonthon.YEIN.post.domain.repository.ScrapRepository;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * Toggles a scrap for the given user and post.
     *
     * If a scrap by the user for the post exists, it is removed and the post's scrap count is decremented.
     * If no scrap exists, a new scrap is created and the post's scrap count is incremented.
     *
     * @param userId the ID of the user performing the toggle
     * @param postId the ID of the post to be scraped or unsaved
     * @return true if a scrap was created; false if an existing scrap was removed
     * @throws GeneralException if the user or post cannot be found (USER_NOT_FOUND or POST_NOT_FOUND)
     */
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

    /**
     * Returns true if the specified user has scraped (bookmarked) the specified post.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @return true if a Scrap exists linking the user and post, otherwise false
     */
    public boolean isScraped(Long userId, Long postId) {
        return scrapRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * Retrieves the User with the given id.
     *
     * @param userId the id of the user to fetch
     * @return the User with the given id
     * @throws GeneralException if no user exists with the provided id (ErrorStatus.USER_NOT_FOUND)
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    /**
     * Retrieves the Post with the given ID.
     *
     * @param postId the identifier of the post to fetch
     * @return the found Post
     * @throws GeneralException if no Post exists for the given ID (ErrorStatus.POST_NOT_FOUND)
     */
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }
}
