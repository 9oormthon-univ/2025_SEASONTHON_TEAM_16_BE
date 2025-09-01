package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<Scrap> findByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT s FROM Scrap s " +
            "WHERE s.user.id = :userId")
    List<Scrap> findByUserId(@Param("userId") Long userId);

    @Query("SELECT s.post.id FROM Scrap s " +
            "WHERE s.user.id = :userId AND s.post.id IN :postIds")
    Set<Long> findScrapedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);
}
