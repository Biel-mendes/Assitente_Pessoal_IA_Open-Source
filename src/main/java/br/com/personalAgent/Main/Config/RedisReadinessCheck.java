package br.com.personalAgent.Main.Config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisReadinessCheck implements ApplicationRunner {

    private final RedisConnectionFactory connectionFactory;

    public RedisReadinessCheck(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int maxAttempts = 15;
        long delayMs = 2000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                connectionFactory.getConnection().ping();
                return;
            } catch (Exception e) {
                Thread.sleep(delayMs);
            }
        }

        throw new IllegalStateException("Redis indisponível após " + maxAttempts + " tentativas — verifique o WSL.");
    }


}