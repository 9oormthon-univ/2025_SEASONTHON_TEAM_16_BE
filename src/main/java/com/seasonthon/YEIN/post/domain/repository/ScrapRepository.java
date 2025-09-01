package com.seasonthon.YEIN.post.domain.repository;

import com.seasonthon.YEIN.post.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    /**
 * Finds a Scrap by the user's id and the post's id.
 *
 * @param userId the id of the user who scraped the post
 * @param postId the id of the post
 * @return an Optional containing the matching Scrap if present, otherwise Optional.empty()
 */
Optional<Scrap> findByUserIdAndPostId(Long userId, Long postId);

    /**
 * Checks whether a scrap exists for the specified user and post.
 *
 * @param userId the user's id
 * @param postId the post's id
 * @return true if a Scrap with the given userId and postId exists; false otherwise
 */
boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * Retrieves all Scrap entities belonging to the specified user.
     *
     * @param userId the id of the user whose scraps should be returned
     * @return a list of Scrap entities associated with the given userId (may be empty)
     */
    @Query("SELECT s FROM Scrap s " +
            "WHERE s.user.id = :userId")
    List<Scrap> findByUserId(@Param("userId") Long userId);

    /**
     * Retrieves the IDs of posts from the given list that the specified user has scraped.
     *
     * @param userId  the ID of the user whose scraps to check
     * @param postIds the candidate post IDs to filter against
     * @return a set of post IDs (unique) from {@code postIds} that the user has scraped
     */
    @Query("SELECT s.post.id FROM Scrap s " +
            "WHERE s.user.id = :userId AND s.post.id IN :postIds")
    Set<Long> findScrapedPostIdsByUserIdAndPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);
}
