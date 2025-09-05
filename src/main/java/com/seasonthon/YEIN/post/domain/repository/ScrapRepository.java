package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
      SELECT s.post FROM Scrap s
      WHERE s.user.id = :userId
      AND (:keyword IS NULL OR
           s.post.title LIKE CONCAT('%', :keyword, '%') OR
           s.post.quote LIKE CONCAT('%', :keyword, '%') OR
           s.post.author LIKE CONCAT('%', :keyword, '%') OR
           s.post.bookTitle LIKE CONCAT('%', :keyword, '%'))
      ORDER BY
          CASE WHEN :sortBy = 'latest' THEN s.post.createdAt END DESC,
          CASE WHEN :sortBy = 'view' THEN s.post.viewCount END DESC,
          CASE WHEN :sortBy = 'scrap' THEN s.post.scrapCount END DESC,
          CASE WHEN :sortBy = 'like' THEN s.post.likeCount END DESC,
          CASE WHEN :sortBy = 'scrap_date' THEN s.createdAt END DESC,
          s.createdAt DESC
      """)
    Page<Post> findScrapedPostsByUserIdWithFilter(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("sortBy") String sortBy, Pageable pageable);

}
