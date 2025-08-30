package com.seasonthon.YEIN.user.domain.repository;

import com.seasonthon.YEIN.global.oauth.domain.SocialProvider;
import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialProviderAndOauthId(SocialProvider socialProvider, String oauthId);
}
