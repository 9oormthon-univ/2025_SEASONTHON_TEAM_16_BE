package com.seasonthon.YEIN.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Configuration
public class JpaConfig {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}