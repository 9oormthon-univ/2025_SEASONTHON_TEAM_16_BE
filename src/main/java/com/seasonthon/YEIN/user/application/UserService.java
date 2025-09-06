package com.seasonthon.YEIN.user.application;

import com.seasonthon.YEIN.gallery.domain.Gallery;
import com.seasonthon.YEIN.gallery.domain.repository.GalleryRepository;
import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.s3.S3UploadService;
import com.seasonthon.YEIN.user.api.dto.response.UpdateProfileResponse;
import com.seasonthon.YEIN.user.api.dto.response.UserProfileResponse;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final GalleryRepository galleryRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public UpdateProfileResponse updateProfile(Long userId, String nickname, MultipartFile profileImage) {
        User user = findUserById(userId);
        
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.updateNickname(nickname);
        }
        
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = uploadProfileImage(profileImage);
            user.updateProfileImage(imageUrl);
        }
        
        return UpdateProfileResponse.from(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Gallery> userGalleries = galleryRepository.findByUser(user);
        double averageScore = userGalleries.stream()
                .mapToDouble(Gallery::getTotalScore)
                .average()
                .orElse(0.0);

        // Calculate today's galleries
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        long todayGalleries = galleryRepository.countByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .averageHandwritingScore(Math.round(averageScore * 100.0) / 100.0)
                .totalGalleries(userGalleries.size())
                .todayGalleries((int) todayGalleries)
                .build();
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
    
    private String uploadProfileImage(MultipartFile file) {
        return s3UploadService.uploadFile(file);
    }
}
