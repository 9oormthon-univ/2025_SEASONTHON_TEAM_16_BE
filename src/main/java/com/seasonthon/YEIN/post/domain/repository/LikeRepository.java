package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Like;
import com.seasonthon.YEIN.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.post.id =:postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId AND l.post.id IN :postIds")
    Set<Long> findLikedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

    @Query("SELECT l.post FROM Like l WHERE l.user.id = :userId")
    Page<Post> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
