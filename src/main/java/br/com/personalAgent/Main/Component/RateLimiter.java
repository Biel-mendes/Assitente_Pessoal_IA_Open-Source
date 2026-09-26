package br.com.personalAgent.Main.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimiter {

    @Value("${app.ratelimit.max-per-minute:60}")
    private int maxRequestsPerMinute;

    private final StringRedisTemplate redisTemplate;

    public RateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String userId) {
        long currentMinute = System.currentTimeMillis() / 60_000;
        String key = "ratelimit:" + userId + ":" + currentMinute;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(65));
        }

        return count != null && count <= maxRequestsPerMinute;
    }

}
