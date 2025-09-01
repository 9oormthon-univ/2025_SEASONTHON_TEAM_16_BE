package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.quote) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'view' THEN p.viewCount END DESC, " +
            "CASE WHEN :sortBy = 'scrap' THEN p.scrapCount END DESC, " +
            "CASE WHEN :sortBy = 'latest' THEN p.createdAt END DESC, " +
            "p.createdAt DESC, p.id DESC")
    Page<Post> findPostsWithFilter(@Param("keyword") String keyword, @Param("sortBy") String sortBy, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.quote) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
    Page<Post> findByUserIdWithFilter(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int incrementViewCount(@Param("id") Long postId);
}
