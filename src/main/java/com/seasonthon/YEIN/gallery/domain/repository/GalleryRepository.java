package com.seasonthon.YEIN.gallery.domain.repository;

import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    Optional<Gallery> findByIdAndUser(Long id, User user);

    Page<Gallery> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // 기간별 조회 - 이번주/이번달/커스텀 기간
    Page<Gallery> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

    // 점수별 조회 - 점수 범위 필터링
    Page<Gallery> findByUserAndTotalScoreBetweenOrderByCreatedAtDesc(User user, Integer minScore, Integer maxScore, Pageable pageable);

    // 기간 + 점수 복합 필터링
    Page<Gallery> findByUserAndCreatedAtBetweenAndTotalScoreBetweenOrderByCreatedAtDesc(User user, LocalDateTime startDate, LocalDateTime endDate,
                                                                                        Integer minScore, Integer maxScore, Pageable pageable);
}
