package com.seasonthon.YEIN.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder kakaoJwtDecoder() {
        String kakaoJwkSetUri = "https://kauth.kakao.com/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(kakaoJwkSetUri).build();
    }
}
