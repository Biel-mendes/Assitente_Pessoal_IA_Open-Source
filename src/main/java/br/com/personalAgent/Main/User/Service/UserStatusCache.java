package br.com.personalAgent.Main.User.Service;

import br.com.personalAgent.Main.User.Model.User;
import br.com.personalAgent.Main.User.Model.UserStatus;
import br.com.personalAgent.Main.User.Repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.UUID;

@Component
public class UserStatusCache {

    private static final String KEY_PREFIX = "user:status";
    private static final Duration TTL = Duration.ofSeconds(30);

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    public UserStatusCache(StringRedisTemplate redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public UserStatus getStatus(UUID userId) {
        String key = KEY_PREFIX + userId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return UserStatus.valueOf(cached);
        }

        UserStatus status = userRepository.findById(userId)
                .map(User::getStatus)
                .orElse(null);

        if (status != null) {
            redisTemplate.opsForValue().set(key, status.name(), TTL);
        }
        return status;
    }

    public void evict(UUID userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}