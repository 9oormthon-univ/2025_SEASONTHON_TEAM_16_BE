package com.seasonthon.YEIN.global.oauth.application;

import com.seasonthon.YEIN.global.code.status.ErrorStatus;
import com.seasonthon.YEIN.global.exception.GeneralException;
import com.seasonthon.YEIN.global.oauth.api.dto.response.LoginResponse;
import com.seasonthon.YEIN.global.oauth.api.dto.response.UserInfoResponse;
import com.seasonthon.YEIN.global.security.CustomUserDetails;
import com.seasonthon.YEIN.global.security.JwtService;
import com.seasonthon.YEIN.user.domain.User;
import com.seasonthon.YEIN.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final IdTokenService idTokenService;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(String idToken) {
        CustomUserDetails userDetails = idTokenService.loadUserByAccessToken(idToken);
        String email = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        String accessToken = jwtService.createAccessToken(email, userId);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.updateRefreshToken(email, refreshToken);
        return new LoginResponse(accessToken, refreshToken);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return new UserInfoResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRoleType()
        );
    }

    @Transactional
    public LoginResponse reissueTokens(String oldAccessToken, String oldRefreshToken) {
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }

        User user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXPIRED_TOKEN));

        String newAccessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.createRefreshToken();

        user.updateRefreshToken(newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        String email = jwtService.verifyTokenAndGetEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateRefreshToken(null);
    }
}
