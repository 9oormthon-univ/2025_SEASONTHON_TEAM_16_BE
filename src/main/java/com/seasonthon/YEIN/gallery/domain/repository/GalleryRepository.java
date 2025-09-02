package com.seasonthon.YEIN.gallery.domain.repository;

import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    Optional<Gallery> findByIdAndUser(Long id, User user);

    @Query("SELECT g FROM Gallery g " +
            "WHERE g.user = :user " +
            "AND (:startDate IS NULL OR g.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR g.createdAt <= :endDate) " +
            "AND (:minScore IS NULL OR g.totalScore >= :minScore) " +
            "AND (:maxScore IS NULL OR g.totalScore <= :maxScore) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'score_desc' THEN g.totalScore END DESC, " +
            "CASE WHEN :sortBy = 'score_asc' THEN g.totalScore END ASC, " +
            "CASE WHEN :sortBy = 'date_asc' THEN g.createdAt END ASC, " +
            "CASE WHEN :sortBy = 'date_desc' OR :sortBy IS NULL THEN g.createdAt END DESC, " +
            "g.id DESC")
    Page<Gallery> findGalleriesWithDynamicSort(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minScore") Integer minScore,
            @Param("maxScore") Integer maxScore,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}
