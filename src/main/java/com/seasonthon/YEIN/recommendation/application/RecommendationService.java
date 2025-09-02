package com.seasonthon.YEIN.recommendation.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasonthon.YEIN.post.domain.Post;
import com.seasonthon.YEIN.post.domain.repository.PostRepository;
import com.seasonthon.YEIN.recommendation.api.dto.response.TodayQuoteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private static final String DAILY_QUOTE_KEY = "daily:quote:";

    private final PostRepository postRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void generateDailyQuote() {
        try {
            String today = LocalDate.now().toString();
            String redisKey = DAILY_QUOTE_KEY + today;

            if (redisTemplate.hasKey(redisKey)) {
                log.info("{} 날짜의 오늘의 문구가 이미 존재합니다", today);
                return;
            }

            TodayQuoteResponse response = createDailyQuote();
            String jsonValue = objectMapper.writeValueAsString(response);

            redisTemplate.opsForValue().set(redisKey, jsonValue, 24, TimeUnit.HOURS);

        } catch (Exception e) {
            log.error("오늘의 문구 생성 중 오류가 발생했습니다", e);
        }
    }

    public TodayQuoteResponse findTodayQuote() {
        String today = LocalDate.now().toString();
        String redisKey = DAILY_QUOTE_KEY + today;

        try {
            String cachedQuote = redisTemplate.opsForValue().get(redisKey);
            if (cachedQuote != null) {
                return objectMapper.readValue(cachedQuote, TodayQuoteResponse.class);
            }

            TodayQuoteResponse newQuote = createDailyQuote();

            try {
                String jsonValue = objectMapper.writeValueAsString(newQuote);
                redisTemplate.opsForValue().set(redisKey, jsonValue, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("오늘의 문구 캐시 저장 중 오류가 발생했습니다", e);
            }

            return newQuote;

        } catch (Exception e) {
            log.warn("캐시된 문구 조회 중 오류가 발생했습니다. 기본 문구를 반환합니다", e);
            return TodayQuoteResponse.defaultQuote();
        }
    }

    private TodayQuoteResponse createDailyQuote() {
        try {
            List<Long> allPostIds = postRepository.findAllPostIds();

            if (allPostIds.isEmpty()) {
                return TodayQuoteResponse.defaultQuote();
            }

            Long randomPostId = selectRandomPostId(allPostIds);
            Optional<Post> postOpt = postRepository.findById(randomPostId);

            if (postOpt.isEmpty()) {
                return TodayQuoteResponse.defaultQuote();
            }

            Post post = postOpt.get();
            return TodayQuoteResponse.from(post.getQuote());

        } catch (Exception e) {
            log.error("오늘의 문구 생성 중 오류가 발생했습니다. 기본 문구를 사용합니다", e);
            return TodayQuoteResponse.defaultQuote();
        }
    }

    private Long selectRandomPostId(List<Long> postIds) {
        int randomIndex = ThreadLocalRandom.current().nextInt(postIds.size());
        return postIds.get(randomIndex);
    }
}
