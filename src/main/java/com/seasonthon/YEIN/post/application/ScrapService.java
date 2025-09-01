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
