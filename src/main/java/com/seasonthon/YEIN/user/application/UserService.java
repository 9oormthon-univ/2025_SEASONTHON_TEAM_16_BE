package com.seasonthon.YEIN.user.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.s3.S3UploadService;
import com.seasonthon.YEIN.user.api.dto.response.UpdateProfileResponse;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
    
    private String uploadProfileImage(MultipartFile file) {
        return s3UploadService.uploadFile(file);
    }
}
