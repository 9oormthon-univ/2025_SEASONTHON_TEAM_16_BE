package com.seasonthon.YEIN.gallery.application;

import com.seasonthon.YEIN.gallery.api.dto.response.GalleryDetailResponse;
import com.seasonthon.YEIN.gallery.api.dto.response.GalleryResponse;
import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.gallery.domain.repository.GalleryRepository;
import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    public Page<GalleryResponse> getGalleriesWithFilters(Long userId, String period, Integer minScore, Integer maxScore, String sortBy, Pageable pageable) {
        LocalDateTime startDate = calculateStartDate(period);
        User user = getUser(userId);

        Page<Gallery> galleries = galleryRepository.findGalleriesWithDynamicSort(
                user,
                startDate,
                LocalDateTime.now(),
                minScore,
                maxScore,
                sortBy,
                pageable
        );

        return galleries.map(this::toGalleryResponse);
    }

    public GalleryDetailResponse getGalleryDetail(Long galleryId, Long userId) {
        User user = getUser(userId);
        Gallery gallery = galleryRepository.findByIdAndUser(galleryId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GALLERY_NOT_FOUND));

        return toGalleryDetailResponse(gallery);
    }

    private LocalDateTime calculateStartDate(String period) {
        if (period == null) return null;

        return switch (period.toLowerCase()) {
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "all" -> null;
            default -> null;
        };
    }

    private GalleryResponse toGalleryResponse(Gallery gallery) {
        return new GalleryResponse(
                gallery.getId(),
                gallery.getImageUrl(),
                gallery.getTotalScore(),
                gallery.getCreatedAt()
        );
    }

    private GalleryDetailResponse toGalleryDetailResponse(Gallery gallery) {
        return new GalleryDetailResponse(
                gallery.getId(),
                gallery.getImageUrl(),
                gallery.getAlignmentScore(),
                gallery.getSpacingScore(),
                gallery.getConsistencyScore(),
                gallery.getLengthScore(),
                gallery.getTotalScore(),
                gallery.getFeedback(),
                gallery.getStrengths(),
                gallery.getDetailedAnalysis(),
                gallery.getCreatedAt()
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}
