package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Returns a paginated list of Posts optionally filtered by a keyword and ordered by a chosen metric.
     *
     * The query filters posts when `keyword` is non-null and non-empty by matching `keyword` against
     * `p.quote` or `p.author` using SQL LIKE (partial match). The `sortBy` parameter selects the
     * ordering:
     * - "view"   → order by viewCount DESC
     * - "scrap"  → order by scrapCount DESC
     * - "latest" → order by createdAt DESC
     *
     * @param keyword  optional search term to match against post quote or author; ignored when null or empty
     * @param sortBy   selects the ordering metric: "view", "scrap", or "latest"
     * @param pageable pagination information
     * @return a Page of Post matching the filter and ordering criteria
     */
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.quote LIKE %:keyword% OR p.author LIKE %:keyword%) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'view' THEN p.viewCount END DESC, " +
            "CASE WHEN :sortBy = 'scrap' THEN p.scrapCount END DESC, " +
            "CASE WHEN :sortBy = 'latest' THEN p.createdAt END DESC")
    Page<Post> findPostsWithFilter(@Param("keyword") String keyword, @Param("sortBy") String sortBy, Pageable pageable);

    /**
     * Returns a page of Posts belonging to the specified user, optionally filtered by a keyword.
     *
     * The keyword, when non-null and non-empty, performs a case-sensitive partial match against
     * the post's `quote` and `author` fields. Results are ordered by `createdAt` descending.
     *
     * @param userId  id of the user whose posts to retrieve
     * @param keyword optional search term applied to `quote` and `author`; ignored if null or empty
     * @param pageable paging and sorting information
     * @return a page of Posts matching the user and keyword filter, ordered by newest first
     */
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.quote LIKE %:keyword% OR p.author LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdWithFilter(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}
