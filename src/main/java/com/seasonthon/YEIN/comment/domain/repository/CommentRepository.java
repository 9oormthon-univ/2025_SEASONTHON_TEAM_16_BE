package com.seasonthon.YEIN.comment.domain.repository;

import com.seasonthon.YEIN.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt ASC")
    Page<Comment> findByPostIdOrderByCreatedAt(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.post " +
            "WHERE c.user.id = :userId " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    long countByPostId(Long postId);
}
