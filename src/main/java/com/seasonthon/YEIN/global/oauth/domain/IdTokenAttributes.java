package com.seasonthon.YEIN.global.oauth.domain;

import com.seasonthon.YEIN.global.oauth.domain.info.KakaoUserInfo;
import com.seasonthon.YEIN.global.oauth.domain.info.UserInfo;
import com.seasonthon.YEIN.user.domain.RoleType;
import com.seasonthon.YEIN.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class IdTokenAttributes {

    private UserInfo userInfo;
    private SocialProvider socialProvider;

    public IdTokenAttributes(Map<String, Object> attributes, SocialProvider socialProvider){
        this.socialProvider = socialProvider;
        if(socialProvider == SocialProvider.KAKAO) this.userInfo = new KakaoUserInfo(attributes);
    }

    public User toUser() {
        return User.builder()
                .socialProvider(socialProvider)
                .roleType(RoleType.USER)
                .oauthId(userInfo.getId())
                .nickname(userInfo.getNickname())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .email(userInfo.getEmail())
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
}
